package edu.uci.eecs.wukong.framework.local;

import edu.uci.eecs.wukong.framework.*;
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

public class FlinkServer extends AbtractActivityClassifier{
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
        DataStream<SensorEvent> classifications = transform(text);

        // print the results with a single thread, rather than in parallel
        classifications.print().setParallelism(1);
        env.execute("Socket Window WordCount");
    }
}
