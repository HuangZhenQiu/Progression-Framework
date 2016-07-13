package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.mptn.packet.MPTNPacket;

/**
 * RPC handler that handles message for devices management
 * 
 * @author peter
 *
 */
public interface RPCCommandHandler {
	
	public void onGetDeviceType(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onRouting(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onDiscover(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onAdd(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onDelete(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onStop(SocketAddress remoteAddress, MPTNPacket bytes);
	
	public void onPoll(SocketAddress remoteAddress, MPTNPacket bytes);
}
