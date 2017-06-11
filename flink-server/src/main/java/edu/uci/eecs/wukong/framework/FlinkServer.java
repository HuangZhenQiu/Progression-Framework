package edu.uci.eecs.wukong.framework;

import edu.uci.eecs.wukong.framework.ActivityDataStream.ActivityWindow;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FlinkServer {
    private static final Logger logger = LoggerFactory.getLogger(FlinkServer.class);

    public static void main(String[] args) throws Exception {
        // get the execution environment
        LocalStreamEnvironment env = new LocalStreamEnvironment();
        // get input data by connecting to the socket
        DataStream<String> text = env.socketTextStream("localhost", 9000, "\n");

        // TopicModel topicModel = new TopicModel();
        // parse the data, group it, window it, and aggregate the counts
        DataStream<SensorEvent> windowCounts = text
                .flatMap(new FlatMapFunction<String, SensorEvent>() {
                    @Override
                    public void flatMap(String raw, Collector<SensorEvent> out) {
                        System.out.println(raw);
                        out.collect(new SensorEvent(raw));
                    }
                })
                .keyBy("key")
                .countWindow(6) // hhl04: 16; cairo: 14
                .reduce(new ReduceFunction<SensorEvent>() {
                    @Override
                    public SensorEvent reduce(SensorEvent a, SensorEvent b) {
                        logger.debug(a.getRaw());
                        // Combine features of multiple event together
                        a.merge(b);
                        return a;
                    }
                }).map(new MapFunction<SensorEvent, SensorEvent>() {
                    @Override
                    public SensorEvent map(SensorEvent value) throws Exception {
                        ActivityWindow window = ActivityDataStream.createActivityWindow(value.getWindowRaw());
                        value.updateAcitityWindow(window); // It is the feature values for topic model
                        // double[] topic probabilities = topicModel.predict(value.getActivityClass());
                        System.out.println("Classification is triggered");
                        //TODO (Bolun) add topic probabilities into original feature list to call random forest
                        return value;
                    }
                });

        // print the results with a single thread, rather than in parallel
        windowCounts.print().setParallelism(1);
        env.execute("Socket Window WordCount");
    }

    // Data type for words with count
    public static class SensorEvent {
        public final String key = "1";
        public String raw;
        public List<String> windowRaw;
        public ActivityWindow activityWindow = null;
        private ActivityClass activityClass = ActivityClass.Other;
        private List<Object> features;

        public SensorEvent() {

        }

        public SensorEvent(String raw) {
            this.raw = raw;
            this.windowRaw = new ArrayList<>();
            this.windowRaw.add(raw);
            this.features =  new ArrayList<>();
        }

        @Override
        public String toString() {
            return raw;
        }

        public void merge(SensorEvent b) {
            this.windowRaw.addAll(b.windowRaw);
        }

        public List<String> getWindowRaw() {
            return windowRaw;
        }

        public void updateAcitityWindow(
                ActivityWindow activityWindow) {
            this.activityWindow = activityWindow;
        }

        public String getKey() {
            return key;
        }

        public String getRaw() {
            return raw;
        }

        public ActivityClass getActivityClass() {
            return activityClass;
        }

        public void setActivityClass(ActivityClass activityClass) {
            this.activityClass = activityClass;
        }

        public List<Object> getFeatures() {
            return features;
        }

        public void setFeatures(List<Object> features) {
            this.features = features;
        }

    }
}
