package edu.uci.eecs.wukong.framework.gateway;

import java.net.SocketAddress;

import edu.uci.eecs.wukong.framework.model.TCPMPTNPackage;

/**
 * Responsible for gateway Id exchange protocol handling
 * 
 * 
 * @author peter
 *
 */
public class IDService implements IDProtocolHandler {

	@Override
	public void onGatewayIDDisover(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoutingTablePing(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRoutingTableRequest(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRountingTableReply(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onForwardRequest(SocketAddress remoteAddress, TCPMPTNPackage bytes) {
		// TODO Auto-generated method stub

	}

}
