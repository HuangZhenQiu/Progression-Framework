package edu.uci.eecs.wukong.framework.nio;

import edu.uci.eecs.wukong.framework.mptn.MPTNMessageListener;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOUdpServer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(NIOUdpServer.class);
	public static int BUFFER_SIZE = 1024;
	private static int THREAD_POOL_SIZE = 10;
	private int port;
	private Selector selector;
	private DatagramChannel serverChannel;
	private List<MPTNMessageListener> listeners;
	private Map<SocketAddress, DatagramChannel> activeChannels;
	private ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	
	public NIOUdpServer(int port) {
		this.port = port;
		this.listeners = new ArrayList<MPTNMessageListener>();
		this.activeChannels = new HashMap<SocketAddress, DatagramChannel> ();
	}
	
	public void addMPTNMessageListener(MPTNMessageListener listener) {
		this.listeners.add(listener);
	}
	
	public void run() {
		try {
			selector = Selector.open();
			serverChannel = DatagramChannel.open();
			InetSocketAddress address = new InetSocketAddress(port);
			serverChannel.socket().bind(address);
			serverChannel.configureBlocking(false);
			serverChannel.register(selector, SelectionKey.OP_READ, new ChannelAttachment());
			while (true) {
				try {
					selector.select();
					Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
					while (selectedKeys.hasNext()) {
						SelectionKey key= (SelectionKey) selectedKeys.next();
						selectedKeys.remove();
						
						if (!key.isValid()) {
							continue;
						}
						
						// We only read from the channel;
						synchronized (key) { 
							
							if (key.isReadable()) {
								read(key);
							}
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
	
	/* Since it is only used for udp device, which only connects with since gateway*/
	public void send(SocketAddress address, ByteBuffer data) {
		try {
			serverChannel.send(data, address);
		} catch (Exception e) {
			logger.error("Fail to send message to address: " + address);
		}
	}
	
	private void read(SelectionKey key) throws IOException {
		DatagramChannel channel = (DatagramChannel) key.channel();
		ChannelAttachment attachment = (ChannelAttachment)key.attachment();
		attachment.setAddress(channel.receive(attachment.getBuffer()));
		attachment.getBuffer().flip();
		DatagramSocket socket = channel.socket();
		SocketAddress remoteAddr = socket.getRemoteSocketAddress();
		if (activeChannels.containsKey(remoteAddr)) {
			activeChannels.put(remoteAddr, channel);
		}
		executorService.execute(
				new EventHandleThread(channel.getRemoteAddress(),
						MPTNUtil.deepCopy(attachment.getBuffer()), listeners));
		attachment.getBuffer().clear();
	}
	
	public void shutdown() {
		try {
			selector.close();
			serverChannel.close();
			executorService.shutdown();
		} catch (IOException e) {
			logger.error(e.toString());
		}
	}
	
	public static void main(String[] args) {
		NIOUdpServer server = new NIOUdpServer(5000);
		server.run();
	}
}
