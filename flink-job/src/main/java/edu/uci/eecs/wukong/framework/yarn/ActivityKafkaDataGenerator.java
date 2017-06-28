package edu.uci.eecs.wukong.framework.yarn;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.*;

import com.alibaba.fastjson.JSON;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActivityKafkaDataGenerator implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(ActivityKafkaDataGenerator.class);

    private static final String KAFKA_SERIALIZER_STRING_ENCODER = "kafka.serializer.StringEncoder";
    private static final String LOCAL_KAFKA_BROKER = "localhost:9092";
    private static final String REQUEST_REQUIRED_ACKS = "request.required.acks";
    private static final String KEY_SERIALIZER_CLASS = "key.serializer.class";
    private static final String METADATA_BROKER_LIST = "metadata.broker.list";
    private String host;
    private String topic;
    private String brokerList;

    public ActivityKafkaDataGenerator(String host, String topic, String brokerList) {
        this.host = host;
        this.topic = topic;
        this.brokerList = brokerList;
    }

    static Properties setUpKafkaProducerConfig(String brokerList) {
        Properties props = new Properties();
        // kafka.broker.list as arg[0]
        props.put(METADATA_BROKER_LIST, StringUtils.isEmpty(brokerList) ? LOCAL_KAFKA_BROKER : brokerList);
        props.put(KEY_SERIALIZER_CLASS, KAFKA_SERIALIZER_STRING_ENCODER);
        props.put(REQUEST_REQUIRED_ACKS, "1");
        return props;
    }

    @Override
    public void run() {
        KafkaProducer producer = new KafkaProducer<String, String>(setUpKafkaProducerConfig(brokerList));

        InputStream inputStream = ActivityKafkaDataGenerator.class.getClassLoader().getResourceAsStream("data.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;

        try {
            while (!StringUtils.isEmpty(line = reader.readLine())) {
                EventData eventData = new EventData(host, line, System.currentTimeMillis());
                producer.send(new ProducerRecord(topic, host, JSON.toJSONString(eventData)));
            }
        } catch (Exception e) {
            logger.error("Failure to send data to client", e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

            } catch (Exception e) {
                logger.error("Failure to release resource acquired", e);
            }
        }

    }

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption("h", "thread", true, "Run how many threads (host home)");
        options.addOption("t", "topic", true, "Run with query type 'small', 'medium' or 'complex'");
        options.addOption("b", "brokerList", true, "Broker list of kafka");

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            if (!cmd.hasOption('h') || !cmd.hasOption("t") || !cmd.hasOption("b")) {
                throw new ParseException("Missing parameters");
            }

            int threadNumber = Integer.parseInt(cmd.getOptionValue('h'));
            String topic = cmd.getOptionValue('t');
            String brokerList = cmd.getOptionValue('b');

            ExecutorService service = Executors.newFixedThreadPool(threadNumber);

            for (int i = 0; i < threadNumber; i++) {
                service.execute(new ActivityKafkaDataGenerator(UUID.randomUUID().toString(), topic, brokerList));
            }
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", options);
        }
    }
}
