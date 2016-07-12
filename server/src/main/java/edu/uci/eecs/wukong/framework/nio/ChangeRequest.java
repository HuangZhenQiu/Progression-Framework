package edu.uci.eecs.wukong.framework.nio;

import java.nio.channels.SocketChannel;

public class ChangeRequest {
	private SocketChannel channel;
	private int opt;
	
	public ChangeRequest(SocketChannel channel, int opt) {
		this.channel = channel;
		this.opt = opt;
	}
	
	public SocketChannel getChannel() {
		return channel;
	}
	
	public int getOpt() {
		return opt;
	}
}
