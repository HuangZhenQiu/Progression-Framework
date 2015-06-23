package edu.uci.eecs.wukong.framework.wkpf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.nio.NIOUdpClient;
import edu.uci.eecs.wukong.framework.nio.NIOUdpServer;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;

public class MPTN implements MPTNMessageListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(MPTN.class);
	private final static Configuration configuration = Configuration.getInstance();	
	private NIOUdpClient gatewayClient;
	private NIOUdpServer server;
	private boolean hasNodeId;
	// Address for communication between IP device with gateway
	private int nodeId; 
	// Address for communication between IP device in network
	private long longAddress;
	
	private List<WKPFMessageListener> listeners;
	private byte HEADER_TYPE_1 = 1;
	private byte HEADER_TYPE_2 = 2;
	
	public MPTN() {
		
		listeners = new ArrayList<WKPFMessageListener>();
		this.hasNodeId = false;
		try {
			this.gatewayClient = new NIOUdpClient(
					configuration.getGatewayIP(), configuration.getGatewayPort());
			this.server = new NIOUdpServer(Integer.parseInt(configuration.getProgressionServerPort()));
			this.server.addMPTNMessageListener(this);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.toString());
		}
	}
	
	public void start() {
		try {
			Thread serverThread = new Thread(server);
			serverThread.start();
			Thread.sleep(1000);
			info();
			while (!hasNodeId) {
				Thread.sleep(1000);
			}
			acquireID();
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Fail to start MPTN");
		}
	}
	
	public void addWKPFMessageListener(WKPFMessageListener listener) {
		this.listeners.add(listener);
	}
	
	public synchronized void info() {
		ByteBuffer buffer = ByteBuffer.allocate(10);
		appendMPTNHeader(buffer, 0, HEADER_TYPE_2);
		gatewayClient.send(buffer.array());
	}
	
	public synchronized void acquireID() {
		
		ByteBuffer buffer = ByteBuffer.allocate(35 /* 10 + 9 + 16*/);
		appendMPTNHeader(buffer, nodeId, HEADER_TYPE_1);
		MPTNUtil.appendMPTNPacket(buffer, MPTNUtil.MPTN_MASTER_ID, MPTNUtil.MPTN_MAX_ID,
				MPTNUtil.MPTN_MSQTYPE_IDREQ, generateUUID());
		buffer.flip();
		gatewayClient.send(buffer.array());
	}
	
	
	private byte[] generateUUID() {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		UUID uuid = UUID.randomUUID();
		bb.putLong(uuid.getMostSignificantBits());
		bb.putLong(uuid.getLeastSignificantBits());
		return bb.array();
	}
	
	/**
	 * Use for send WKPF message
	 * @param destId
	 * @param payload
	 */
	public void send(int destId, byte[] payload) {
		int size = payload.length + 10;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		appendMPTNHeader(buffer, destId, HEADER_TYPE_1);
		MPTNUtil.appendMPTNPacket(buffer, nodeId, destId,
				MPTNUtil.MPTN_MSATYPE_FWDREQ, payload);
	}
	
	private void appendMPTNHeader(ByteBuffer buffer, int nodeId, byte type) {
		int ipaddress = MPTNUtil.IPToInteger(configuration.getGatewayIP());
		short port = configuration.getGatewayPort();
		MPTNUtil.appendMPTNHeader(buffer, ipaddress, port, nodeId, type);
	}

	public void onMessage(ByteBuffer message) {
		int p1 = message.get() & 0xFF;
		int p2 = message.get() & 0xFF;
		if (p1 == 0xAA && p2 == 0x55) {
			processInfoMessage(message.get());
		} else {
			message.flip();
			int destId = message.getInt();
			int srcId = message.getInt();
			byte messageType = message.get();
			if (destId == nodeId) { 
				if (messageType == MPTNUtil.MPTN_MSATYPE_FWDREQ) {
					processFWDMessage(message.slice());
				} else if (messageType == MPTNUtil.MPTN_MSQTYPE_IDACK) {
					processIDMessage(destId);
				} else if (messageType == MPTNUtil.MPTN_MSQTYPE_GWOFFER) {
					LOGGER.info("Gateway Discovery is not implemented.");
				} else {
					LOGGER.error("Received wrong type of messages from :" +  message.toString());
				}
			} else {
				LOGGER.error("Received message not target to progression server: " + message.toString());
			}
		}
	}
	
	/**
	 * Handle the response for INFO response.
	 * 
	 * @param message
	 */
	private void processInfoMessage(int nodeId) {
		this.nodeId = nodeId;
		this.hasNodeId = true;
		LOGGER.debug("Recevied Node Id: " + nodeId);
	}
	
	
	/**
	 * Handle the response for ID acquire.
	 * 
	 * @param message
	 */
	private void processIDMessage(int longAddress) {
		this.longAddress = longAddress;
		LOGGER.debug("Received Long Address from gateway: " + longAddress);
	}
	
	/**
	 * Handle WKPF message.
	 *   byte 0      : command
	 *	 byte 1, 2   : seqnr
	 *	 bytes 3+    : payload
	 * @param message WKPF Message
	 */
	private void processFWDMessage(ByteBuffer message) {
		byte[] payload = message.array();
		
		switch(payload[0]) {
			case WKPFUtil.REPRG_OPEN:
				fireWKPFRemoteProgram(payload);
			case WKPFUtil.REPRG_WRITE:
				fireWKPFRemoteProgram(payload);
			case WKPFUtil.REPRG_COMMIT:
				fireWKPFRemoteProgram(payload);
			case WKPFUtil.WKPF_GET_WUCLASS_LIST:
				fireWKPFGetWuClassList(payload);
			case WKPFUtil.WKPF_GET_WUOBJECT_LIST:
				fireWKPFGetWuObjectList(payload);
			case WKPFUtil.WKPF_READ_PROPERTY:
				fireWKPFReadProperty(payload);
			case WKPFUtil.WKPF_WRITE_PROPERTY:
				fireWKPFWriteProperty(payload);
			case WKPFUtil.WKPF_GET_LOCATION:
				fireWKPFGetLocation(payload);
			case WKPFUtil.WKPF_SET_LOCATION:
				fireWKPFSetLocation(payload);
			case WKPFUtil.MONITORING:
				fireWKPFMonitoredData(payload);
			default:
				LOGGER.error("Received unpexcted WKPF message type " + payload[0]);
		}
	}
	
	private void fireWKPFRemoteProgram(byte[] message) {
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgram(message);
		}
	}
	
	private void fireWKPFGetWuClassList(byte[] message) {
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetWuClassList(message);
		}
	}
	
	private void fireWKPFGetWuObjectList(byte[] message) {
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetWuObjectList(message);
		}
	}
	
	private void fireWKPFReadProperty(byte[] message) {
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFReadProperty(message);
		}
	}
	
	private void fireWKPFWriteProperty(byte[] message) {
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFWriteProperty(message);
		}
	}
	
	private void fireWKPFMonitoredData(byte[] message) {
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFMonitoredData(message);
		}
	}
	
	private void fireWKPFGetLocation(byte[] message) {
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetLocation(message);
		}
	}
	
	private void fireWKPFSetLocation(byte[] message) {
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFSetLocation(message);
		}
	}
}
