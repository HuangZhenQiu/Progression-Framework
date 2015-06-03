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
	private ByteBuffer sendBuffer;
	private ByteBuffer receiveBuffer;
	private DatagramChannel channel;
	private SocketAddress server;
	
	public NIOUdpClient(String domain, int port) throws IOException {
		this.sendBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		this.sendBuffer.order(ByteOrder.BIG_ENDIAN);
		this.receiveBuffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
		this.receiveBuffer.order(ByteOrder.BIG_ENDIAN);
		this.channel = DatagramChannel.open();
		this.server = new InetSocketAddress(domain, port);
		SocketAddress address = new InetSocketAddress(0);
		DatagramSocket socket = channel.socket();
		socket.setSoTimeout(3000);
		socket.bind(address);
	}
	
	public synchronized void send(byte[] value) {
		try {
			sendBuffer.clear();
			sendBuffer.put(value);
			sendBuffer.flip();
			channel.send(sendBuffer, server);
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
			for (int i = 0; i< 100; i++) {
				//client.send(i, i, i);
			}
			client.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
