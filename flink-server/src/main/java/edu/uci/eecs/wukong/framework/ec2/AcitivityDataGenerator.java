package edu.uci.eecs.wukong.framework.ec2;

import com.amazonaws.kinesis.producer.*;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import edu.uci.eecs.wukong.framework.Utils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simulate thousands of hosts send activity message to EC2 kinesis cluster.
 * https://github.com/awslabs/amazon-kinesis-client
 *
 */
public class AcitivityDataGenerator implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(AcitivityDataGenerator.class);
    private static final int DATA_SIZE = 128;
    private static final int SECONDS_TO_RUN = 5;
    private static final int RECORDS_PER_SECOND = 2000;
    private static final int DEFAULT_TIME_OUT = 6000;
    public static final String STREAM_NAME = "wukong-activity";
    public static final String REGION = "us-west-1";
    private KinesisProducer producer;
    private AtomicLong sequenceNumber = new AtomicLong(0);
    final AtomicLong completed = new AtomicLong(0);

    public AcitivityDataGenerator(){
        producer = createProducer();
    }

    private KinesisProducer createProducer() {
        Configuration config = new Configuration();
        config.setRegion(REGION);
        config.setAwsAccessKeyId("replace with real id");
        config.setAwsSecretKey("replace with real key");
        config.setMaxConnections(1);
        config.setConnectTimeout(6000);
        KinesisProducer producer = new KinesisProducer(config);
        return producer;
    }

    final FutureCallback<UserRecordResult> callback = new FutureCallback<UserRecordResult>() {
        @Override
        public void onFailure(Throwable t) {
            // We don't expect any failures during this sample. If it
            // happens, we will log the first one and exit.
            if (t instanceof UserRecordFailedException) {
                Attempt last = Iterables.getLast(
                        ((UserRecordFailedException) t).getResult().getAttempts());
                logger.error(String.format(
                        "Record failed to put - %s : %s",
                        last.getErrorCode(), last.getErrorMessage()));
            }
            logger.error("Exception during put", t);
            System.exit(1);
        }

        @Override
        public void onSuccess(UserRecordResult result) {
            completed.getAndIncrement();
        }
    };

    @Override
    public void run() {

        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("data.txt");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;

        try {
            while (!StringUtils.isEmpty(line = reader.readLine())) {
                line += "\n";

                ByteBuffer data = ByteBuffer.wrap(line.getBytes("UTF-8"));
                ListenableFuture<UserRecordResult> f =
                        producer.addUserRecord(STREAM_NAME, Long.toString(System.currentTimeMillis()),
                                Utils.randomExplicitHashKey(), data);
                Futures.addCallback(f, callback);
                logger.info(String.format("Sending out event %s", line));
                Thread.sleep(10);
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
}
