package edu.uci.eecs.wukong.framework.wkpf;

import io.netty.channel.socket.DatagramChannel;

public interface MPTNMessageListener {
	public void onMessage(DatagramChannel channel, byte[] message);
}
