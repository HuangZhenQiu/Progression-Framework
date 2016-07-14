package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.mptn.packet.MPTNPacket;

public interface IDProtocolHandler {
	
	public void onGatewayIDDisover(SocketAddress remoteAddress, long nouce, MPTNPacket bytes);
	
	public void onRoutingTablePing(SocketAddress remoteAddress, long nouce, MPTNPacket bytes);
	
	public void onRoutingTableRequest(SocketAddress remoteAddress, long nouce, MPTNPacket bytes);
	
	public void onRountingTableReply(SocketAddress remoteAddress, long nouce, MPTNPacket bytes);
	
	public void onForwardRequest(SocketAddress remoteAddress, long nouce, MPTNPacket bytes);
	
	public void onGatewayIDRequest(SocketAddress remoteAddress, long nouce, MPTNPacket bytes);
	
	public void onRPCCommand(SocketAddress remoteAddress, long nouce,  MPTNPacket bytes);
}
