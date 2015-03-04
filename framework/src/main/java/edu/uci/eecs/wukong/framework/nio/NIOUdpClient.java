package edu.uci.eecs.wukong.framework.nio;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NIOUdpClient {
	private static Logger logger = LoggerFactory.getLogger(NIOUdpClient.class);
	private static final int BUFFER_SIZE = 1024;
	private String domain;
	private int port;
	private ByteBuffer buffer;
	private DatagramChannel channel;
	private SocketAddress server;
	
	public NIOUdpClient(String domain, int port) throws IOException {
		this.domain = domain;
		this.port = port;
		this.buffer = ByteBuffer.allocateDirect(1024);
		this.buffer.order(ByteOrder.BIG_ENDIAN);
		this.channel = DatagramChannel.open();
		this.server = new InetSocketAddress(domain, port);
	}
	
	public void connect() throws Exception {
		SocketAddress address = new InetSocketAddress(0);
		DatagramSocket socket = channel.socket();
		socket.setSoTimeout(1000);
		socket.bind(address);
	}
	
	public void send(int device, int port, int value) {
		try {
			buffer.clear();
			buffer.putInt(device);
			buffer.putInt(port);
			buffer.putInt(value);
			buffer.flip();
			channel.send(buffer, server);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
	
	public void close() throws Exception {
		this.channel.close();
	}
	
	public static void main(String[] args) {
		
		try {
			NIOUdpClient client = new NIOUdpClient("localhost", 8000);
			client.connect();
			for (int i = 0; i< 100; i++) {
				client.send(i, i, i);
			}
			client.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
