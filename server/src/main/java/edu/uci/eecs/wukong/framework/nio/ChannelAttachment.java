package edu.uci.eecs.wukong.framework.nio;

import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class ChannelAttachment {
	private ByteBuffer buffer;
	private SocketAddress address;
	
	public ChannelAttachment() {
		this.buffer = ByteBuffer.allocate(NIOUdpServer.BUFFER_SIZE);
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public SocketAddress getAddress() {
		return address;
	}

	public void setAddress(SocketAddress address) {
		this.address = address;
	}
}
