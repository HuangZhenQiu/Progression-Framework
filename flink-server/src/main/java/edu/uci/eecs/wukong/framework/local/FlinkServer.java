package edu.uci.eecs.wukong.framework.local;

import edu.uci.eecs.wukong.framework.*;
import edu.uci.eecs.wukong.framework.ActivityDataStream.ActivityWindow;
import org.apache.flink.api.common.functions.*;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.dropwizard.metrics.DropwizardMeterWrapper;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Meter;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.util.Collector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;

public class FlinkServer {
    private static final Logger logger = LoggerFactory.getLogger(FlinkServer.class);

    // The local env can't load flink correctly
    private static Configuration createConfiguration() throws Exception {
        // get the execution environment
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("flink-conf.yaml");
        Properties properties = new Properties();
        properties.load(stream);
        Configuration conf = new Configuration();
        Enumeration<String> names = (Enumeration<String>) properties.propertyNames();
        while(names.hasMoreElements()) {
            String name = names.nextElement();
            String value = properties.getProperty(name);
            conf.setString(name, value);
        }

        return conf;
    }
    public static void main(String[] args) throws Exception {

        Configuration conf = createConfiguration();
        LocalStreamEnvironment env = new LocalStreamEnvironment(conf);
        // get input data by connecting to the socket
        DataStream<String> text = env.socketTextStream("localhost", 9000, "\n");

        // parse the data, group it, window it, and aggregate the counts
        DataStream<SensorEvent> windowCounts = text
                .flatMap(new FlatMapFunction<String, SensorEvent>() {
                    @Override
                    public void flatMap(String raw, Collector<SensorEvent> out) {
                        System.out.println(raw);
                        out.collect(new SensorEvent(raw, System.currentTimeMillis()));
                    }
                })
                .keyBy("key")
                .countWindow(14) // hhl04: 16; cairo: 14
                .reduce(new ReduceFunction<SensorEvent>(){
                    @Override
                    public SensorEvent reduce(SensorEvent a, SensorEvent b) {
                        logger.debug(a.getRaw());
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
//                        ActivityWindow window = ActivityDataStream.createActivityWindow(value.getWindowRaw());
//                        value.updateAcitityWindow(window); // It is the feature values for topic model
                        // double[] topic probabilities = topicModel.predict(value.getActivityClass());
                        double [] final_features = value.extractFeatures(tm, matrix);
                        rf.predictByFinalFeatures(final_features);
                        System.out.println("Classification is triggered");
                        //TODO (Bolun) add topic probabilities into original feature list to call random forest
                        recordExecutionTime.markEvent(System.currentTimeMillis() - value.timeStamp);
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
        public long timeStamp;
        public String raw;
        public List<String> windowRaw;
        public ActivityWindow activityWindow = null;
        private ActivityClass activityClass = ActivityClass.Other;
        private List<Features> slidingWindow;

        public SensorEvent() {

        }

        public SensorEvent(String raw, long timeStamp) {
            this.raw = raw;
            this.timeStamp = timeStamp;
            this.windowRaw = new ArrayList<>();
            this.windowRaw.add(raw);
            this.slidingWindow =  new ArrayList<Features>();
            this.slidingWindow.add(new Features(raw));
        }

        @Override
        public String toString() {
            return raw;
        }

        public void merge(SensorEvent b) {

            //this.windowRaw.addAll(b.windowRaw);

            this.slidingWindow.addAll(b.slidingWindow);
            Collections.sort(this.slidingWindow, new Comparator<Features>() {
                @Override
                public int compare(Features lhs, Features rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.daystamp.compareTo(rhs.daystamp) > 0 ? -1 : (lhs.daystamp.compareTo(rhs.daystamp) < 0) ? 1 : 0;
                }
            });
            this.windowRaw.addAll(b.windowRaw);
//            this.activityClass = this.slidingWindow.get(this.slidingWindow.size() - 1).current_act;
        }

        public List<String> getWindowRaw() {
            return windowRaw;
        }

        public double[] extractFeatures(TopicModel tm, MutualInfoMatrix mutualInfoMatrix){
            int parent_act = this.slidingWindow.get(this.slidingWindow.size() - 1).current_act, current_act = parent_act;
            Timestamp daytime_start, daytime_end;
            daytime_start = this.slidingWindow.get(0).daystamp;
            daytime_end = this.slidingWindow.get(this.slidingWindow.size() - 1).daystamp;
            int sensor_start_id =  this.slidingWindow.get(0).sensor_id;
            int sensor_last_id = this.slidingWindow.get(this.slidingWindow.size() - 1).sensor_id;
            Date sd=new Date(daytime_start.getTime()); Calendar cd=Calendar.getInstance(); cd.setTime(sd);
            int wd = (cd.get(Calendar.DAY_OF_WEEK)+6)%7;
            Date ed=new Date(daytime_end.getTime()); Calendar cd2=Calendar.getInstance(); cd2.setTime(ed);


            double[] ret = new double[8+SensorClass.values().length+ActivityClass.values().length];

            double weekday = Math.round ((double)wd/7.0 * 1000.0) / 1000.0;
            ret[0] = weekday;
            double ts = ((cd.get(Calendar.HOUR_OF_DAY)) +(cd.get(Calendar.MINUTE)/60.0)), te = ((cd2.get(Calendar.HOUR_OF_DAY)) +(cd2.get(Calendar.MINUTE)/60.0));
            double time_start =Math.round((ts/24.0*1000.0)/1000.0);
            ret[1] = time_start;
            double time_end = Math.round((te/24.0*1000.0)/1000.0);
            ret[2] = time_end;
            double timespan = Math.round((daytime_end.getTime() - daytime_start.getTime())/1000.0/(12*3600)*1000.0)/1000.0;
            ret[3] = timespan;
            double parent_act_d = Math.round(((double)parent_act/ActivityClass.values().length)*1000.0)/1000.0;
            ret[4] = parent_act_d;
            double sensor_start = Math.round(((double)sensor_start_id/ SensorClass.values().length)*1000.0)/1000.0;
            ret[5] = sensor_start;
            double sensor_last = Math.round(((double)sensor_last_id/ SensorClass.values().length)*1000.0)/1000.0;
            ret[6] = sensor_last;
            double sensor_list_size = Math.round(((double)this.slidingWindow.size()/ SensorClass.values().length)*1000.0)/1000.0;
            ret[7] = sensor_list_size;

            int count = 8;
            int row = sensor_last_id;

            double [][] matrix = mutualInfoMatrix.getMatrix();
            double [] matrix_row = matrix[row];
            double [] mut_info = new double[SensorClass.values().length];

            for (SensorClass sensor : SensorClass.values()){
                int col = sensor.ordinal();
                double value = 0.0;
                for (Features f: this.slidingWindow){
                    if(f.sensor_id == col){
                        value = matrix_row[col];
                        break;
                    }
                }
                mut_info[col] = value;
                ret[count+col] = Math.round(value*1000.0)/1000.0;
            }

            int[] lda_sensor_list = new int[SensorClass.values().length];
            for (int i = 0; i < mut_info.length; ++i){
                if (mut_info[i] != 0.0){
                    lda_sensor_list[i] = 1;
                } else {
                    lda_sensor_list[i] = 0;
                }

            }
            count = SensorClass.values().length + 8;
            double[] lda_features = tm.inferHighLevelFeatures(extractLDAFeatures(wd, ts, te, parent_act, lda_sensor_list));
            for (int i = 0; i < lda_features.length; i++){
                ret[i+count] = lda_features[i];
            }
            return ret;
        }

        public int[] extractLDAFeatures(int weekday, double  time_start, double time_end, int parent_act, int[] lda_sensor_list){
            int [] ret = new int[7+24+ActivityClass.values().length+lda_sensor_list.length];
            int count = 0;
            for (int i = 0; i < 7; i++, count++){
                if(weekday != i){
                    ret[i] = 0;
                } else {
                    ret[i] = 1;
                }
            }
            for (int i = 0; i < 24; i++){
                if(i >= time_start && i <= time_end){
                    ret[i+count] = 1;
                } else {
                    ret[i+count] = 0;
                }
            }
            count += 24;
            for (int i = 0; i < ActivityClass.values().length; i++){
                if(parent_act != i){
                    ret[i+count] = 0;
                } else {
                    ret[i+count] = i;
                }
            }
            count += ActivityClass.values().length;
            for (int i = 0; i< lda_sensor_list.length; i++, count++){
                ret[i+count] = lda_sensor_list[i];
            }
            return ret;
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

        public List<Features> getFeatures() {
            return slidingWindow;
        }

        public void setFeatures(List<Features> features) {
            this.slidingWindow = features;
        }

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public void setRaw(String raw) {
            this.raw = raw;
        }

        public void setWindowRaw(List<String> windowRaw) {
            this.windowRaw = windowRaw;
        }

        public ActivityWindow getActivityWindow() {
            return activityWindow;
        }

        public void setActivityWindow(ActivityWindow activityWindow) {
            this.activityWindow = activityWindow;
        }

        public List<Features> getSlidingWindow() {
            return slidingWindow;
        }

        public void setSlidingWindow(List<Features> slidingWindow) {
            this.slidingWindow = slidingWindow;
        }
    }
}
