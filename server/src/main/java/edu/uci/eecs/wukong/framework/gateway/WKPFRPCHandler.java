package edu.uci.eecs.wukong.framework.gateway;

import edu.uci.eecs.wukong.framework.model.TCPMPTNPackage;

import java.net.SocketAddress;

/**
 * RPC handler that handles message for devices management
 * 
 * @author peter
 *
 */
public interface WKPFRPCHandler {
	
	public void onSend(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onGetDeviceType(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onRouting(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onDiscover(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onAdd(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onDelete(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onStop(SocketAddress remoteAddress, TCPMPTNPackage bytes);
	
	public void onPoll(SocketAddress remoteAddress, TCPMPTNPackage bytes);
}
