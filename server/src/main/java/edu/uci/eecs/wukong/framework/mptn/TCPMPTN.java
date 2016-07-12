package edu.uci.eecs.wukong.framework.mptn;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.gateway.IDProtocolHandler;
import edu.uci.eecs.wukong.framework.nio.NIOTCPServer;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class TCPMPTN extends AbstractMPTN implements MPTNMessageListener<TCPMPTNPackage> {
	private final static Logger LOGGER = LoggerFactory.getLogger(TCPMPTN.class);
	private final static Configuration configuration = Configuration.getInstance();	
	private NIOTCPServer server;
	private List<IDProtocolHandler> listeners;
	
	public TCPMPTN() {
		this.listeners = new ArrayList<IDProtocolHandler> ();
		this.server = new NIOTCPServer(configuration.getGatewayPort());
		this.server.addMPTNMessageListener(this);
		Thread serverThread = new Thread(server);
		serverThread.start();
	}

	
	public void register(IDProtocolHandler listener) {
		this.listeners.add(listener);
	}
	

	@Override
	public void onMessage(SocketAddress remoteAddress, TCPMPTNPackage message) {
		if (message.getLength() >= 9) {
			MPTNPackage mptn = new MPTNPackage(message.getPayload());
			switch(mptn.getType() & 0xFF) {
				case MPTN_MSGTYPE_GWDISCOVER & 0xFF:
					fireGatewayIDDisover(remoteAddress, mptn);
					break;
				case MPTN_MSGTYPE_RTPING & 0xFF:
					fireRoutingTablePing(remoteAddress, mptn);
					break;
				case MPTN_MSGTYPE_RTREQ & 0xFF:
					fireRoutingTableRequest(remoteAddress, mptn);
					break;
				case MPTN_MSGTYPE_RTREP & 0xFF:
					fireRountingTableReply(remoteAddress, mptn);
					break;
				case MPTN_MSGTYPE_GWIDREQ & 0xFF:
					fireGatewayIDRequest(remoteAddress, mptn);
					break;
				case MPTN_MSGTYPE_FWDREQ & 0xFF:
					fireForwardRequest(remoteAddress, mptn);
					break;
				case MPTN_MSGTYPE_RPCCMD & 0xFF:
					fireRPCCommand(remoteAddress, mptn);
				default:
					LOGGER.error("Received unpexcted MPTN message type " + mptn);
			}
		} else {
			LOGGER.error("Received unpexcted MPTN message with length less than 9 bytes");
		}
	}
	
	private void fireGatewayIDDisover(SocketAddress remoteAddress, MPTNPackage bytes) {
		for (IDProtocolHandler handler : listeners) {
			handler.onGatewayIDDisover(remoteAddress, bytes);
		}
	}
	
	private void fireRoutingTablePing(SocketAddress remoteAddress, MPTNPackage bytes) {
		for (IDProtocolHandler handler : listeners) {
			handler.onRoutingTablePing(remoteAddress, bytes);
		}
	}
	
	private void fireRoutingTableRequest(SocketAddress remoteAddress, MPTNPackage bytes) {
		for (IDProtocolHandler handler : listeners) {
			handler.onRoutingTableRequest(remoteAddress, bytes);
		}
	}
	
	private void fireRountingTableReply(SocketAddress remoteAddress, MPTNPackage bytes) {
		for (IDProtocolHandler handler : listeners) {
			handler.onRountingTableReply(remoteAddress, bytes);
		}
	}
	
	private void fireForwardRequest(SocketAddress remoteAddress, MPTNPackage bytes) {
		for (IDProtocolHandler handler : listeners) {
			handler.onForwardRequest(remoteAddress, bytes);
		}
	}
	
	private void fireGatewayIDRequest(SocketAddress remoteAddress, MPTNPackage bytes) {
		for (IDProtocolHandler handler : listeners) {
			handler.onGatewayIDRequest(remoteAddress, bytes);
		}
	}
	
	private void fireRPCCommand(SocketAddress remoteAddress, MPTNPackage bytes) {
		for (IDProtocolHandler handler : listeners) {
			handler.onRPCCommand(remoteAddress, bytes);
		}
	}
}
