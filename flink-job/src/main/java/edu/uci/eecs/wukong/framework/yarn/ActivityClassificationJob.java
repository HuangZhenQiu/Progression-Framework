package edu.uci.eecs.wukong.framework.yarn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import edu.uci.eecs.wukong.framework.*;
import org.apache.commons.cli.*;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Meter;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * The flink job that consume data from amazon-kinesis cluster. It does the same activity classification as
 * FlinkServer. The difference is that it run as a distributed job, and it uses session window for each host
 * key.
 *
 */
public class ActivityClassificationJob extends AbtractActivityClassifier {
    private static final Logger logger = LoggerFactory.getLogger(ActivityClassificationJob.class);

    public ActivityClassificationJob() {

    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("b", "bootstrap.servers", true, "Bootstrap Servers");
        options.addOption("z", "zookeeper.connect", true, "Zookeeper Connect");
        options.addOption("w", "windowSize", true, "Window Size of Activity Sequence");

        CommandLineParser parser = new DefaultParser();

        try {

            CommandLine cmd = parser.parse(options, args);
            if (!cmd.hasOption('b') || !cmd.hasOption("z") || !cmd.hasOption("w")) {
                throw new ParseException("Missing parameters");
            }

            String brokerList = cmd.getOptionValue('b');
            String zookeeper = cmd.getOptionValue('z');
            int windowSize = Integer.parseInt(cmd.getOptionValue('w'));

            Properties properties = new Properties();
            properties.setProperty("bootstrap.servers", brokerList);
            properties.setProperty("zookeeper.connect", zookeeper);
            properties.setProperty("group.id", "test");
            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

            FlinkKafkaConsumer010<String> kafkaConsumer =
                    new FlinkKafkaConsumer010("activity", new SimpleStringSchema(), properties);

            DataStream<String> stream = env
                    .addSource(kafkaConsumer, TypeInformation.of(String.class));

            // parse the data, group it, window it, and aggregate the counts
            DataStream<SensorEvent> windowCounts = stream
                    .flatMap(new FlatMapFunction<String, SensorEvent>() {
                        @Override
                        public void flatMap(String raw, Collector<SensorEvent> out) {
                            EventData eventData = JSON.parseObject(raw, new TypeReference<EventData>() {});
                            out.collect(new SensorEvent(eventData.getHostID(), eventData.getEvent(), eventData.getTimestamp(),
                                    System.currentTimeMillis()));
                        }
                    })
                    .keyBy("host")
                    .countWindow(windowSize) // hhl04: 16; cairo: 14
                    .reduce(new ReduceFunction<SensorEvent>() {
                        @Override
                        public SensorEvent reduce(SensorEvent a, SensorEvent b) {
    //                         logger.debug(a.getRaw());
                            // Combine slidingWindow of multiple event together
                            a.merge(b);
                            return a;
                        }

                    }).map(new RichMapFunction<SensorEvent, SensorEvent>() {
                        private com.codahale.metrics.Meter executionMeter;
                        private com.codahale.metrics.Meter e2eMeter;
                        private com.codahale.metrics.Meter processMeter;
                        private Meter e2eTime;
                        private Meter processTime;
                        private Meter recordExecutionTime;
                        private Counter counter;
                        private TopicModel tm;
                        private RandomForest rf;
                        MutualInfoMatrix matrix;

                        @Override
                        public void open(Configuration parameters) throws Exception {
                            executionMeter = new com.codahale.metrics.Meter();
                            e2eMeter = new com.codahale.metrics.Meter();
                            processMeter = new com.codahale.metrics.Meter();
                            recordExecutionTime = getRuntimeContext().getMetricGroup().meter("executionMeter",
                                    new DropwizardMeterWrapper(executionMeter));
                            e2eTime = getRuntimeContext().getMetricGroup().meter("e2eMeter",
                                    new DropwizardMeterWrapper(e2eMeter));
                            counter = getRuntimeContext().getMetricGroup().counter("windowCounter");
                            processTime = getRuntimeContext().getMetricGroup().meter("processMeter",
                                    new DropwizardMeterWrapper(processMeter));
                            tm = TopicModel.createByDefault();
                            rf = RandomForest.createByDefault();
                            matrix = MutualInfoMatrix.createByDefaultFile();
                        }

                        @Override
                        public SensorEvent map(SensorEvent value) throws Exception {
                            double[] final_features = value.extractFeatures(tm, matrix);
                            long start = System.currentTimeMillis();
                            int label = rf.predictByFinalFeatures(final_features);
                            long end = System.currentTimeMillis();
                            recordExecutionTime.markEvent(end - value.getArrivalTime());
                            e2eTime.markEvent(end - value.getTimeStamp());
                            processTime.markEvent(end - start);
                            counter.inc();
                            return value;
                        }
                    });
            // print the results with a single thread, rather than in parallel
            windowCounts.print().setParallelism(1);
            env.execute("Flink Activity Classification");
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", options);
        }
    }
}
