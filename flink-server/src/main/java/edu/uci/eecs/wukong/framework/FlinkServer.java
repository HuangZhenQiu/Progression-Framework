package edu.uci.eecs.wukong.framework;

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
        // parse the data, group it, window it, and aggregate the counts
        DataStream<SensorEvent> windowCounts = text
                .flatMap(new FlatMapFunction<String, SensorEvent>() {
                    @Override
                    public void flatMap(String raw, Collector<SensorEvent> out) {
                        System.out.println(raw);
                        //TODO (Bolun) add the feature exaction logic
                        out.collect(new SensorEvent(raw));
                    }
                })
                .keyBy("key")
                .countWindow(6)
                .reduce(new ReduceFunction<SensorEvent>() {
                    @Override
                    public SensorEvent reduce(SensorEvent a, SensorEvent b) {
                        logger.debug(a.getRaw());
                        // Combine features of multiple event together
                        a.appendFeatures(b.raw, b.features);
                        return a;
                    }
                }).map(new MapFunction<SensorEvent, SensorEvent>() {

                    @Override
                    public SensorEvent map(SensorEvent value) throws Exception {
                        //TODO (Bolun)
                        // 1) apply topic model on windowFeatures
                        // 2) do the classification on random forest and change the activityClass field of SensorEvent

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
        private ActivityClass activityClass;
        private List<Object> features = new ArrayList<>();
        private Map<String, List<Object>> windowFeatures = new HashMap<>();

        public SensorEvent() {
            activityClass = ActivityClass.Other;
        }

        public SensorEvent(String raw) {
            this.raw = raw;
        }

        @Override
        public String toString() {
            return raw;
        }

        public void generateFeatures() {
            //It is the place to add features into the sensorEvent
            windowFeatures.put(raw, features);
        }

        public void appendFeatures(String raw, List<Object> features) {
            windowFeatures.put(raw, features);
        }

        public String getKey() {
            return key;
        }

        public String getRaw() {
            return raw;
        }

        public void setRaw(String raw) {
            this.raw = raw;
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

        public Map<String, List<Object>> getWindowFeatures() {
            return windowFeatures;
        }

        public void setWindowFeatures(Map<String, List<Object>> windowFeatures) {
            this.windowFeatures = windowFeatures;
        }
    }

    public enum ActivityClass {
        Other,
        Bed_to_toilet,
        Breakfast,
        R1_sleep,
        R1_wake,
        R1_work_in_office,
        Dinner,
        Laundry,
        Leave_home,
        Lunch,
        Night_wandering,
        R2_sleep,
        R2_take_medicine,
        R2_wake
    }
}
