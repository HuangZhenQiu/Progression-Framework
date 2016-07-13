package edu.uci.eecs.wukong.framework.mptn;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.mptn.packet.AbstractMPTNPacket;

public interface MPTNMessageListener<T extends AbstractMPTNPacket> {
	
	public void onMessage(SocketAddress remoteAddress, T message);
}
