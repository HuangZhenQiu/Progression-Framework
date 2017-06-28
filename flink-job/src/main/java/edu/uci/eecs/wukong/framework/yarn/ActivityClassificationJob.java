package edu.uci.eecs.wukong.framework.yarn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import edu.uci.eecs.wukong.framework.*;
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
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
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

    public void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", args[0]);
        properties.setProperty("zookeeper.connect", args[1]);
        properties.setProperty("group.id", "test");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        FlinkKafkaConsumer09<String> kafkaConsumer =
                new FlinkKafkaConsumer09("activity", new SimpleStringSchema(), properties);

        DataStream<String> stream = env
                .addSource(kafkaConsumer, TypeInformation.of(String.class));

        // parse the data, group it, window it, and aggregate the counts
        DataStream<SensorEvent> windowCounts = stream
                .flatMap(new FlatMapFunction<String, SensorEvent>() {
                    @Override
                    public void flatMap(String raw, Collector<SensorEvent> out) {
                        EventData eventData = JSON.parseObject(raw, new TypeReference<EventData>() {});
                        out.collect(new SensorEvent(eventData.getHostID(), eventData.getEvent(),
                                eventData.getTimestamp()));
                    }
                })
                .keyBy("host")
                .countWindow(14) // hhl04: 16; cairo: 14
                .reduce(new ReduceFunction<SensorEvent>() {
                    @Override
                    public SensorEvent reduce(SensorEvent a, SensorEvent b) {
//                         logger.debug(a.getRaw());
                        // Combine slidingWindow of multiple event together
                        a.merge(b);
                        return a;
                    }

                }).map(new RichMapFunction<SensorEvent, SensorEvent>() {
                    private com.codahale.metrics.Meter meter;
                    private Meter recordExecutionTime;
                    private Counter counter;
                    private TopicModel tm;
                    private RandomForest rf;
                    MutualInfoMatrix matrix;

                    @Override
                    public void open(Configuration parameters) throws Exception {
                        meter = new com.codahale.metrics.Meter();
                        recordExecutionTime = getRuntimeContext().getMetricGroup().meter("executionMeter",
                                new DropwizardMeterWrapper(meter));
                        counter = getRuntimeContext().getMetricGroup().counter("windowCounter");
                        tm = TopicModel.createByDefault();
                        rf = RandomForest.createByDefault();
                        matrix = MutualInfoMatrix.createByDefaultFile();
                    }

                    @Override
                    public SensorEvent map(SensorEvent value) throws Exception {
                        double[] final_features = value.extractFeatures(tm, matrix);
                        int label = rf.predictByFinalFeatures(final_features);
                        recordExecutionTime.markEvent(value.elaspsedTime);
                        return value;
                    }
                });



        // print the results with a single thread, rather than in parallel
        windowCounts.print().setParallelism(1);
        env.execute("Flink Activity Classification");
    }
}
