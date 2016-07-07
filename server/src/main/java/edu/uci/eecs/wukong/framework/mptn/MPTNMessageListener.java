package edu.uci.eecs.wukong.framework.mptn;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.model.MPTNPackage;

public interface MPTNMessageListener {
	public void onMessage(SocketAddress remoteAddress, MPTNPackage bytes);
}
