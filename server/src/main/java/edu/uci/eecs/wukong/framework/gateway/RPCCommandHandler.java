package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.mptn.MPTNPackage;

/**
 * RPC handler that handles message for devices management
 * 
 * @author peter
 *
 */
public interface RPCCommandHandler {
	
	public void onSend(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onGetDeviceType(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onRouting(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onDiscover(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onAdd(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onDelete(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onStop(SocketAddress remoteAddress, MPTNPackage bytes);
	
	public void onPoll(SocketAddress remoteAddress, MPTNPackage bytes);
}
