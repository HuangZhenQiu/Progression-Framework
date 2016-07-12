package edu.uci.eecs.wukong.framework.mptn;

import java.net.SocketAddress;

public interface MPTNMessageListener<T extends AbstractMPTNPackage> {
	
	public void onMessage(SocketAddress remoteAddress, T message);
}
