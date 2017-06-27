package edu.uci.eecs.wukong.framework;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Meter;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.util.Collector;

public class AbtractActivityClassifier {
    public static DataStream<SensorEvent> transform(DataStream<String> inputStream) {
        // parse the data, group it, window it, and aggregate the counts
        DataStream<SensorEvent> windowCounts = inputStream
                .flatMap(new FlatMapFunction<String, SensorEvent>() {
                    @Override
                    public void flatMap(String raw, Collector<SensorEvent> out) {
                        System.out.println(raw);
                        out.collect(new SensorEvent(raw, System.currentTimeMillis()));
                    }
                })
                .keyBy("key")
                .countWindow(14) // hhl04: 16; cairo: 14
                .reduce(new ReduceFunction<SensorEvent>() {
                    @Override
                    public SensorEvent reduce(SensorEvent a, SensorEvent b) {
                        // logger.debug(a.getRaw());
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
                        rf = new RandomForest();
                        matrix = MutualInfoMatrix.createByDefaultFile();
                    }

                    @Override
                    public SensorEvent map(SensorEvent value) throws Exception {
                        double[] final_features = value.extractFeatures(tm, matrix);
                        int label = rf.predictByFinalFeatures(final_features);
                        System.out.println("Classification is triggered : it's " + ActivityClass.values()[label].toString());
                        //TODO (Bolun) add topic probabilities into original feature list to call random forest
                        recordExecutionTime.markEvent(System.currentTimeMillis() - value.timeStamp);
                        return value;
                    }
                });
        return windowCounts;
    }
}
