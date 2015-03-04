package edu.uci.eecs.wukong.framework.nio;

import edu.uci.eecs.wukong.framework.ProgressionKey.PhysicalKey;
import edu.uci.eecs.wukong.framework.manager.BufferManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOUdpServer {
	private static Logger logger = LoggerFactory.getLogger(NIOUdpServer.class);
	private static int BUFFER_SIZE = 1024;
	private static int PROPRESSION_PORT = 8000;
	private BufferManager bufferManager;
	
	protected NIOUdpServer() {
		// Only used for testing
	}
	
	public NIOUdpServer(BufferManager bufferManager) {
		this.bufferManager = bufferManager;
	}
	
	private static class ChannelAttachment{
		private ByteBuffer buffer;
		private SocketAddress address;
		
		public ChannelAttachment() {
			this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
		}
	}
	
	public void start() {
		try {
			Selector selector = Selector.open();
			DatagramChannel channel = DatagramChannel.open();
			InetSocketAddress address = new InetSocketAddress(PROPRESSION_PORT);
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
			logger.error(e.toString());
		}
	}
	
	private void read(SelectionKey key) throws IOException {
		DatagramChannel channel = (DatagramChannel) key.channel();
		ChannelAttachment attachment = (ChannelAttachment)key.attachment();
		attachment.address = channel.receive(attachment.buffer);
		System.out.println(Arrays.toString(attachment.buffer.array()));
		// TODO(HuangZhenqiu) Add buffer in the client size to prove the transmission speedy.
		int size = attachment.buffer.position() / 6;
		for (int i =0; i < size; i++) {
			short deviceId = attachment.buffer.getShort(i * 6);
			short portId = attachment.buffer.getShort(i * 6 + 2);
			short value = attachment.buffer.getShort(i * 6 + 4);
			// Force to convert to int to reduce size.
			bufferManager.addData(new PhysicalKey(deviceId, portId), (int)System.currentTimeMillis(), value);
		}
		attachment.buffer.clear();
		// TODO put data into dispatcher.
	}
	
	public static void main(String[] args) {
		NIOUdpServer server = new NIOUdpServer();
		server.start();
	}
}
