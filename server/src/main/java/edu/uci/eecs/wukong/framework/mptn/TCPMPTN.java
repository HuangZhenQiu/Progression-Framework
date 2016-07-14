package edu.uci.eecs.wukong.framework.mptn;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.gateway.IDProtocolHandler;
import edu.uci.eecs.wukong.framework.mptn.packet.MPTNPacket;
import edu.uci.eecs.wukong.framework.mptn.packet.TCPMPTNPacket;
import edu.uci.eecs.wukong.framework.nio.NIOTCPServer;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;

public class TCPMPTN extends AbstractMPTN implements MPTNMessageListener<TCPMPTNPacket> {
	private final static Logger LOGGER = LoggerFactory.getLogger(TCPMPTN.class);
	private final static Configuration configuration = Configuration.getInstance();	
	private NIOTCPServer server;
	private List<IDProtocolHandler> listeners;
	private Map<Integer, SocketAddress> connectedSockets;
	private long currentNouce = 0;
	
	public TCPMPTN() {
		this.listeners = new ArrayList<IDProtocolHandler> ();		
		this.connectedSockets = new HashMap<Integer, SocketAddress>();
		SocketAddress masterAddress = new InetSocketAddress(configuration.getMasterAddress(), configuration.getMasterTCPPort());
		this.connectedSockets.put(MPTNUtil.MASTER_ID, masterAddress);
		this.server = new NIOTCPServer(masterAddress, configuration.getGatewayPort());
		this.server.addMPTNMessageListener(this);
		Thread serverThread = new Thread(server);
		serverThread.start();
	}

	
	public void register(IDProtocolHandler listener) {
		this.listeners.add(listener);
	}
	
	public MPTNPacket send(int destId, MPTNPacket message, boolean expectReply) {
		if (connectedSockets.containsKey(destId)) {
			currentNouce ++;
			byte[] payload = message.asByteArray();
			TCPMPTNPacket packet = new TCPMPTNPacket(destId, currentNouce, payload.length, payload);			
			MPTNPacket result = server.send(connectedSockets.get(destId), destId, packet, true);
			if (expectReply) {
				if (result != null) {
					return result;
				}
				LOGGER.error("Fail to receive reply from " + destId);
			}
		}
		return null;
	}
	

	@Override
	public void onMessage(SocketAddress remoteAddress, TCPMPTNPacket message) {
		if (message.getLength() >= 9) {
			MPTNPacket mptn = new MPTNPacket(message.getPayload());
			switch(mptn.getType() & 0xFF) {
				case MPTNUtil.MPTN_MSGTYPE_GWDISCOVER & 0xFF:
					fireGatewayIDDisover(remoteAddress, mptn);
					break;
				case MPTNUtil.MPTN_MSGTYPE_RTPING & 0xFF:
					fireRoutingTablePing(remoteAddress, mptn);
					break;
				case MPTNUtil.MPTN_MSGTYPE_RTREQ & 0xFF:
					fireRoutingTableRequest(remoteAddress, mptn);
					break;
				case MPTNUtil.MPTN_MSGTYPE_RTREP & 0xFF:
					fireRountingTableReply(remoteAddress, mptn);
					break;
				case MPTNUtil.MPTN_MSGTYPE_GWIDREQ & 0xFF:
					fireGatewayIDRequest(remoteAddress, mptn);
					break;
				case MPTNUtil.MPTN_MSGTYPE_FWDREQ & 0xFF:
					fireForwardRequest(remoteAddress, mptn);
					break;
				case MPTNUtil.MPTN_MSGTYPE_RPCCMD & 0xFF:
					fireRPCCommand(remoteAddress, mptn);
				default:
					LOGGER.error("Received unpexcted MPTN message type " + mptn);
			}
		} else {
			LOGGER.error("Received unpexcted MPTN message with length less than 9 bytes");
		}
	}
	
	private void fireGatewayIDDisover(SocketAddress remoteAddress, MPTNPacket bytes) {
		LOGGER.info("Received GatewayIDDisover MPTN message");
		for (IDProtocolHandler handler : listeners) {
			handler.onGatewayIDDisover(remoteAddress, bytes);
		}
	}
	
	private void fireRoutingTablePing(SocketAddress remoteAddress, MPTNPacket bytes) {
		LOGGER.info("Received RoutingTablePing MPTN message");
		for (IDProtocolHandler handler : listeners) {
			handler.onRoutingTablePing(remoteAddress, bytes);
		}
	}
	
	private void fireRoutingTableRequest(SocketAddress remoteAddress, MPTNPacket bytes) {
		LOGGER.info("Received RoutingTableRequest MPTN message");
		for (IDProtocolHandler handler : listeners) {
			handler.onRoutingTableRequest(remoteAddress, bytes);
		}
	}
	
	private void fireRountingTableReply(SocketAddress remoteAddress, MPTNPacket bytes) {
		LOGGER.info("Received RountingTableReply MPTN message");
		for (IDProtocolHandler handler : listeners) {
			handler.onRountingTableReply(remoteAddress, bytes);
		}
	}
	
	private void fireForwardRequest(SocketAddress remoteAddress, MPTNPacket bytes) {
		LOGGER.info("Received ForwardRequest MPTN message");
		for (IDProtocolHandler handler : listeners) {
			handler.onForwardRequest(remoteAddress, bytes);
		}
	}
	
	
	private void fireGatewayIDRequest(SocketAddress remoteAddress, MPTNPacket bytes) {
		LOGGER.info("Received GatewayIDRequest MPTN message");
		for (IDProtocolHandler handler : listeners) {
			handler.onGatewayIDRequest(remoteAddress, bytes);
		}
	}
	
	private void fireRPCCommand(SocketAddress remoteAddress, MPTNPacket bytes) {
		LOGGER.info("Received RPCCommand MPTN message");
		for (IDProtocolHandler handler : listeners) {
			handler.onRPCCommand(remoteAddress, bytes);
		}
	}
}
