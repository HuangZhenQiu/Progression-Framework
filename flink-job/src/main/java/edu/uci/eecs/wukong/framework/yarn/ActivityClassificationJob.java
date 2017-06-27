package edu.uci.eecs.wukong.framework.yarn;

import edu.uci.eecs.wukong.framework.*;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;
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
                new FlinkKafkaConsumer09("topic", new SimpleStringSchema(), properties);

        DataStream<String> stream = env
                .addSource(kafkaConsumer, TypeInformation.of(String.class));

        // parse the data, group it, window it, and aggregate the counts
        DataStream<SensorEvent> windowCounts = transform(stream);
        // print the results with a single thread, rather than in parallel
        windowCounts.print().setParallelism(1);
        env.execute("Socket Window WordCount");
    }
}
