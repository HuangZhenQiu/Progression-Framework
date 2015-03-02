package edu.uci.eecs.wukong.framework.nio;

import edu.uci.eecs.wukong.framework.dispatch.Dispatcher;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOUdpServer {
	private static Logger logger = LoggerFactory.getLogger(NIOUdpServer.class);
	private static int BUFFER_SIZE = 1024;
	private static int PROPRESSION_PORT = 8000;
	private Dispatcher dispatcher;
	
	protected NIOUdpServer() {
		
	}
	
	public NIOUdpServer(Dispatcher dispatcher) {
		this.dispatcher = dispatcher;
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
		int size = attachment.buffer.position() / 12;
		/*for (int i =0; i < size; i++) {
			System.out.println(attachment.buffer.getInt(i * 12) + ":"
				+ attachment.buffer.getInt(i * 12 + 4) + ":" + attachment.buffer.getInt(i * 12 + 8));
		}*/
		attachment.buffer.clear();
		// TODO put data into dispatcher.
	}
	
	public static void main(String[] args) {
		NIOUdpServer server = new NIOUdpServer();
		server.start();
	}
}
