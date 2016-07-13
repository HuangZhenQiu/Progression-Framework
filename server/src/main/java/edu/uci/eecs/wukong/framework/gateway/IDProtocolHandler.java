package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.mptn.packet.MPTNPacket;

public interface IDProtocolHandler {
	
	public void onGatewayIDDisover(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onRoutingTablePing(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onRoutingTableRequest(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onRountingTableReply(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onForwardRequest(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onGatewayIDRequest(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onRPCCommand(SocketAddress remoteAddress, MPTNPacket bytes);
}
