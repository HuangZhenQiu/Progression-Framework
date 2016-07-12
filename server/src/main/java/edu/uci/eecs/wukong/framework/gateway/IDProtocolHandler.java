package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.mptn.MPTNPackage;

public interface IDProtocolHandler {
	
	public void onGatewayIDDisover(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onRoutingTablePing(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onRoutingTableRequest(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onRountingTableReply(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onForwardRequest(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onGatewayIDRequest(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onRPCCommand(SocketAddress remoteAddress, MPTNPackage bytes);
}
