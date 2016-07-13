package edu.uci.eecs.wukong.framework.gateway.rpc;

import java.net.SocketAddress;


/**
 * RPC handler that handles message for devices management
 * 
 * @author peter
 *
 */
public interface RPCCommandHandler {
	
	public TinyRPCResponse onGetDeviceType(SocketAddress remoteAddress, TinyRPCRequest bytes);
	
	public TinyRPCResponse onRouting(SocketAddress remoteAddress, TinyRPCRequest bytes);
	
	public TinyRPCResponse onDiscover(SocketAddress remoteAddress, TinyRPCRequest bytes);
	
	public TinyRPCResponse onAdd(SocketAddress remoteAddress, TinyRPCRequest bytes);
	
	public TinyRPCResponse onDelete(SocketAddress remoteAddress, TinyRPCRequest bytes);
	
	public TinyRPCResponse onStop(SocketAddress remoteAddress, TinyRPCRequest bytes);
	
	public TinyRPCResponse onPoll(SocketAddress remoteAddress, TinyRPCRequest bytes);
	
	public TinyRPCResponse onSend(SocketAddress remoteAddress, TinyRPCRequest bytes);
}
