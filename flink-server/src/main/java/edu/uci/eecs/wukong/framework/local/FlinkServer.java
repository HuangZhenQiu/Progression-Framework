package edu.uci.eecs.wukong.framework.local;

import edu.uci.eecs.wukong.framework.*;
import edu.uci.eecs.wukong.framework.metrics.JvmMetrics;
import edu.uci.eecs.wukong.framework.metrics.MetricsRegistryHolder;
import edu.uci.eecs.wukong.framework.metrics.reporter.GraphiteMetricsReporter;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.graphite.GraphiteReporter;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

public class FlinkServer extends AbtractActivityClassifier{
//    private static final Logger logger = LoggerFactory.getLogger(FlinkServer.class);

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

    public static void SequentialTest() throws Exception{
        InputStream inputStream = FlinkServer.class.getClassLoader().getResourceAsStream("data.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        Queue<SensorEvent> myqueue = new LinkedList<SensorEvent>();
        TopicModel tm = TopicModel.createByDefault();
        RandomForest rf = RandomForest.createByDefault();
        MutualInfoMatrix matrix = MutualInfoMatrix.createByDefaultFile();

        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
        PrintWriter pw = new PrintWriter(new File("/tmp/flink-log-"+dateFormat.format(date)+".csv"));

        int FULLSIZE = 14;
        int count = 1008;
        while(!StringUtils.isEmpty(line = reader.readLine())  && count-- > 0){
            myqueue.add(new SensorEvent(line, System.nanoTime(), System.nanoTime()));
            if (myqueue.size() == FULLSIZE){
                ArrayList<SensorEvent> list = new ArrayList<SensorEvent>(myqueue);
                SensorEvent value = list.get(0);
                for (int i = 1; i < FULLSIZE; i++){
                    value.merge(list.get(i));
                }

                long startTime = System.nanoTime();
                double[] final_features = value.extractFeatures(tm, matrix);
                int label = rf.predictByFinalFeatures(final_features);
                long endTime = System.nanoTime();

                value.elaspsedTime = endTime - value.timeStamp;
                value.processingTime = endTime - startTime;

                StringBuilder sb = new StringBuilder();
                sb.append(value.timeStamp);
                sb.append(',');
                sb.append(value.elaspsedTime);
                sb.append(',');
                sb.append(value.processingTime);
                sb.append('\n');

                pw.write(sb.toString());

                myqueue.remove();
            }
        }
        pw.flush();
        pw.close();
    }

    public static void main(String[] args) throws Exception {
        GraphiteMetricsReporter reporter = new GraphiteMetricsReporter("Reporter");
        MetricsRegistryHolder holder = new MetricsRegistryHolder();
        JvmMetrics jvmMetrics = new JvmMetrics(holder);
        reporter.register("Flink-Server", holder);
        reporter.start();
        Configuration conf = createConfiguration();
        LocalStreamEnvironment env = new LocalStreamEnvironment(conf);
        // get input data by connecting to the socket
//        DataStream<String> text = env.socketTextStream(args[0], Integer.parseInt(args[1]), "\n");
        DataStream<String> text = env.socketTextStream("localhost", 9000, "\n");

        // parse the data, group it, window it, and aggregate the counts
        DataStream<SensorEvent> classifications = transform(text);

        // print the results with a single thread, rather than in parallel
//        classifications.print().setParallelism(1);
        env.execute("Socket Window WordCount");
//        SequentialTest();

    }
}
