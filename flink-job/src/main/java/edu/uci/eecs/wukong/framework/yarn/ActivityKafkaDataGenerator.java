package edu.uci.eecs.wukong.framework.yarn;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ActivityKafkaDataGenerator {
    private static final String KAFKA_SERIALIZER_STRING_ENCODER = "kafka.serializer.StringEncoder";
    private static final String LOCAL_KAFKA_BROKER = "localhost:9092";
    private static final String REQUEST_REQUIRED_ACKS = "request.required.acks";
    private static final String KEY_SERIALIZER_CLASS = "key.serializer.class";
    private static final String METADATA_BROKER_LIST = "metadata.broker.list";

    static Properties setUpKafkaProducerConfig(String[] args) {
        Properties props = new Properties();
        // kafka.broker.list as arg[0]
        props.put(METADATA_BROKER_LIST, (args.length == 1) ? args[0] : LOCAL_KAFKA_BROKER);
        props.put(KEY_SERIALIZER_CLASS, KAFKA_SERIALIZER_STRING_ENCODER);
        props.put(REQUEST_REQUIRED_ACKS, "1");
        return props;
    }

    public static void main(String[] args) {
        KafkaProducer producer = new KafkaProducer<String, String>(setUpKafkaProducerConfig(args));

    }
}
