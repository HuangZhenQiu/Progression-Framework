package edu.uci.eecs.wukong.framework.local;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityDataReplyer {
    private static final Logger logger = LoggerFactory.getLogger(ActivityDataReplyer.class);
    private ServerSocket serverSocket;
    private ExecutorService executorService;

    public ActivityDataReplyer(int port) throws Exception {
        serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(5);
    }

    public void run(int sleepms, int datasize) throws Exception {
        while (true) {
            Socket connectionSocket = serverSocket.accept();
            logger.info(String.format("Received request from %s", connectionSocket.getInetAddress()));
            executorService.execute(new DataWriter(connectionSocket, sleepms, datasize));
        }
    }

    private class DataWriter implements Runnable {
        private Socket socket;
        private DataOutputStream outputStream;
        private int sleepms;
        private int datasize;

        public DataWriter(Socket socket, int sleepms, int datasize) throws Exception {
            this.socket = socket;
            this.outputStream = new DataOutputStream(socket.getOutputStream());
            this.sleepms = sleepms;
            this.datasize = datasize;
        }

        @Override
        public void run() {
            logger.info("Start to execute data replay");
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("data.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;

            try {
                int count = datasize;
                while (!StringUtils.isEmpty(line = reader.readLine()) && count-- > 0) {
                    line += "\n";
                    outputStream.writeBytes(line);
                    outputStream.flush();
                    if(count % 1000 == 0) {
                        logger.info(String.format("countdown %d total %d ", count, datasize));
                    }
//                    logger.info(String.format("Sending out event %s", line));
                    Thread.sleep(sleepms);
                }
            } catch (Exception e) {
                logger.error("Failure to send data to client", e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (Exception e) {
                    logger.error("Failure to release resource acquired", e);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
//        ActivityDataReplyer dataReplyer = new ActivityDataReplyer(Integer.parseInt(args[0]));
        ActivityDataReplyer dataReplyer = new ActivityDataReplyer(9000);
//        dataReplyer.run(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        dataReplyer.run(10, 16*1008);
    }
}
