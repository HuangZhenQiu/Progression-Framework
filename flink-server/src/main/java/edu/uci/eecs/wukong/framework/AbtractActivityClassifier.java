package edu.uci.eecs.wukong.framework;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Meter;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.flink.core.fs.FileSystem.WriteMode.OVERWRITE;

public class AbtractActivityClassifier {
    public static DataStream<SensorEvent> transform(DataStream<String> inputStream) {

        // parse the data, group it, window it, and aggregate the counts
        DataStream<SensorEvent> windowCounts = inputStream
                .flatMap(new FlatMapFunction<String, SensorEvent>() {
                    @Override
                    public void flatMap(String raw, Collector<SensorEvent> out) {
//                        System.out.println(raw);
                        out.collect(new SensorEvent(raw, System.nanoTime(), System.nanoTime()));
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
                        long startTime = System.nanoTime();
                        double[] final_features = value.extractFeatures(tm, matrix);
                        int label = rf.predictByFinalFeatures(final_features);
                        long endTime = System.nanoTime();
                        //TODO (Bolun) add topic probabilities into original feature list to call random forest
                        value.elaspsedTime = endTime - value.timeStamp;
                        value.processingTime = endTime - startTime;
                        recordExecutionTime.markEvent(value.elaspsedTime);
//                        System.out.println("Classification is triggered : it's " + ActivityClass.values()[label].toString());
                        return value;
                    }
                });

        DataStream<Tuple3<Long, Long, Long>> timeStream = windowCounts.map(
                new MapFunction<SensorEvent, Tuple3<Long, Long, Long>>() {
                    @Override
                    public Tuple3<Long, Long, Long> map(SensorEvent value) {
                        return new Tuple3<>(value.timeStamp, value.elaspsedTime, value.processingTime);
                    }
                }
        );
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
        timeStream.writeAsCsv("/tmp/flink-log-"+dateFormat.format(date)+".csv", OVERWRITE);

        return windowCounts;
    }
}
