package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.mptn.MPTNPackage;

/**
 * Responsible for gateway Id exchange protocol handling
 * 
 * 
 * @author peter
 *
 */
public class IDService implements IDProtocolHandler {
	private Gateway gateway;
	
	public IDService(Gateway gateway) {
		this.gateway = gateway;
	}

	@Override
	public void onGatewayIDDisover(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoutingTablePing(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoutingTableRequest(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRountingTableReply(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onForwardRequest(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGatewayIDRequest(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRPCCommand(SocketAddress remoteAddress, MPTNPackage bytes) {
		// TODO Auto-generated method stub
		
	}

}
