package edu.uci.eecs.wukong.framework.nio;

import edu.uci.eecs.wukong.framework.wkpf.MPTNMessageListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOUdpServer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(NIOUdpServer.class);
	public static int BUFFER_SIZE = 1024;
	private static int THREAD_POOL_SIZE = 10;
	private int port;
	private List<MPTNMessageListener> listeners;
	private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	
	public NIOUdpServer(int port) {
		this.port = port;
		this.listeners = new ArrayList<MPTNMessageListener>();
	}
	
	public void addMPTNMessageListener(MPTNMessageListener listener) {
		this.listeners.add(listener);
	}
	
	public void run() {
		try {
			Selector selector = Selector.open();
			DatagramChannel channel = DatagramChannel.open();
			InetSocketAddress address = new InetSocketAddress(port);
			channel.socket().bind(address);
			channel.configureBlocking(false);
			SelectionKey clientKey = channel.register(selector, SelectionKey.OP_READ);
			clientKey.attach(new ChannelAttachment());
			while (true) {
				try {
					selector.select();
					Iterator selectedKeys = selector.selectedKeys().iterator();
					while (selectedKeys.hasNext()) {
						SelectionKey key= (SelectionKey) selectedKeys.next();
						selectedKeys.remove();
						
						if (!key.isValid()) {
							continue;
						}
						
						// We only read from the channel;
						if (key.isReadable()) {
							executorService.execute(new EventHandleThread(key, listeners));
						}
					}
					
				} catch (IOException e) {
					logger.error(e.toString());
				}
			}
		} catch (IOException e) { 
			e.printStackTrace();
			logger.error(e.toString());
		}
	}
	
	private void read(SelectionKey key) throws IOException {
		
	}
	
	public static void main(String[] args) {
		NIOUdpServer server = new NIOUdpServer(5000);
		server.run();
	}
}
