package edu.uci.eecs.wukong.framework.ec2;

import org.apache.flink.streaming.api.scala.DataStream;
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment;
import org.apache.flink.streaming.util.serialization.SimpleStringSchema;

import java.util.Properties;

/**
 * The flink job that consume data from amazon-kinesis cluster. It does the same activity classification as
 * FlinkServer. The difference is that it run as a distributed job, and it uses session window for each host
 * key.
 *
 */
public class ActivityClassificationJob {

    public void main(String[] args) {
        Properties consumerConfig = new Properties();
        /*consumerConfig.put(ConsumerConfigConstants.AWS_REGION, "us-east-1");
        consumerConfig.put(ConsumerConfigConstants.AWS_ACCESS_KEY_ID, "aws_access_key_id");
        consumerConfig.put(ConsumerConfigConstants.AWS_SECRET_ACCESS_KEY, "aws_secret_access_key");
        consumerConfig.put(ConsumerConfigConstants.STREAM_INITIAL_POSITION, "LATEST");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<String> kinesis = env.addSource(new FlinkKinesisConsumer<>(
                "kinesis_stream_name", new SimpleStringSchema(), consumerConfig));*/
    }
}
