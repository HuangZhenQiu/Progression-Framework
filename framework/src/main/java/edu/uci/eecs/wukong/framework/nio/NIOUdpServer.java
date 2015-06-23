package edu.uci.eecs.wukong.framework.nio;

import edu.uci.eecs.wukong.framework.wkpf.MPTNMessageListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOUdpServer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(NIOUdpServer.class);
	private static int BUFFER_SIZE = 1024;
	private int port;
	private List<MPTNMessageListener> listeners;
	
	public NIOUdpServer(int port) {
		this.port = port;
		this.listeners = new ArrayList<MPTNMessageListener>();
	}
	
	private static class ChannelAttachment{
		private ByteBuffer buffer;
		private SocketAddress address;
		
		public ChannelAttachment() {
			this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
		}
	}
	
	public void addMPTNMessageListener(MPTNMessageListener listener) {
		this.listeners.add(listener);
	}
	
	public void fireMPTNMessage(ByteBuffer bytes) {
		for (MPTNMessageListener listener : listeners) {
			listener.onMessage(bytes);
		}
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
						try {
							SelectionKey key= (SelectionKey) selectedKeys.next();
							selectedKeys.remove();
							
							if (!key.isValid()) {
								continue;
							}
							
							// We only read from the channel;
							if (key.isReadable()) {
								read(key);
							}
						} catch(IOException e) {
							logger.error(e.toString());
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
		DatagramChannel channel = (DatagramChannel) key.channel();
		ChannelAttachment attachment = (ChannelAttachment)key.attachment();
		attachment.address = channel.receive(attachment.buffer);
		attachment.buffer.flip();
		fireMPTNMessage(attachment.buffer.duplicate());
		attachment.buffer.clear();
	}
	
	public static void main(String[] args) {
		NIOUdpServer server = new NIOUdpServer(5000);
		server.run();
	}
}
