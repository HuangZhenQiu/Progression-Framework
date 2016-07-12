package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.model.TCPMPTNPackage;

public interface IDProtocolHandler {
	
	public void onGatewayIDDisover(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onRoutingTablePing(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onRoutingTableRequest(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onRountingTableReply(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onForwardRequest(SocketAddress remoteAddress, TCPMPTNPackage bytes);
}
