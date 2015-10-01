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
	private int nodeId = -1; 
	// Address for communication between IP device in network
	private int longAddress;
	
	private List<WKPFMessageListener> listeners;
	private static int MPTN_HEADER_LENGTH = 9;
	private static byte HEADER_TYPE_1 = 1;
	private static byte HEADER_TYPE_2 = 2;
	
	public MPTN() {
		
		listeners = new ArrayList<WKPFMessageListener>();
		this.hasNodeId = false;
		try {
			this.gatewayClient = new NIOUdpClient(
					configuration.getGatewayIP(), configuration.getGatewayPort());
			this.server = new NIOUdpServer(configuration.getProgressionServerPort());
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
	
	public void register(WKPFMessageListener listener) {
		this.listeners.add(listener);
	}
	
	public synchronized void info() {
		ByteBuffer buffer = ByteBuffer.allocate(11);
		appendMPTNHeader(buffer, MPTNUtil.MPTN_MASTER_ID, HEADER_TYPE_2, (byte)0);
		gatewayClient.send(buffer.array());
	}
	
	public synchronized void acquireID() {
		
		ByteBuffer buffer = ByteBuffer.allocate(36 /* 11 + 9 + 16*/);
		appendMPTNHeader(buffer, nodeId, HEADER_TYPE_1, (byte)25);
		MPTNUtil.appendMPTNPacket(buffer, MPTNUtil.MPTN_MAX_ID.intValue(), MPTNUtil.MPTN_MASTER_ID,
				MPTNUtil.MPTN_MSQTYPE_IDREQ, generateUUID());
		gatewayClient.send(buffer.array());
	}
	
	
	private byte[] generateUUID() {
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		UUID uuid = UUID.randomUUID();
		bb.putLong(uuid.getLeastSignificantBits());
		bb.putLong(uuid.getMostSignificantBits());
		return bb.array();
	}
	
	/**
	 * Use for send WKPF message
	 * @param destId
	 * @param payload
	 */
	public void send(int destId, byte[] payload) {
		int size = payload.length + 20;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		appendMPTNHeader(buffer, nodeId, HEADER_TYPE_1, (byte)(payload.length + 9));
		MPTNUtil.appendMPTNPacket(buffer, longAddress, destId,
				MPTNUtil.MPTN_MSATYPE_FWDREQ, payload);
		gatewayClient.send(buffer.array());
	}
	
	private void appendMPTNHeader(ByteBuffer buffer, int nodeId, byte type, byte payload_bytes) {
		int ipaddress = MPTNUtil.IPToInteger(configuration.getProgressionServerIP());
		short port = configuration.getProgressionServerPort();
		MPTNUtil.appendMPTNHeader(buffer, ipaddress, port, nodeId, type, payload_bytes);
	}

	public void onMessage(ByteBuffer message) {
		if (validateMPTNHeader(message)) {
			// The 11th byte is the length of the WKPF message
			byte type = message.get();
			int length = (int)message.get();
			if (type == HEADER_TYPE_1) { 
				processFWDMessage(message, length);
			} else if (type == HEADER_TYPE_2){
				if (length == 1) {
					processInfoMessage(message.get());
				} else {
					processIDMessage(message.getInt());
				}
			} else {
				LOGGER.error("Received message error message type");
			}
		}
	}
	
	private boolean validateMPTNHeader(ByteBuffer message) {
		byte[] header = new byte[MPTN_HEADER_LENGTH];
		message.get(header);
		int p1 = header[0] & 0xFF;
		int p2 = header[1] & 0xFF;
		int destId = header[2] & 0xFF;
		if (p1 != 0xAA || p2 != 0x55) {
			LOGGER.error("Received Incorrect Wukong Message.");
			return false;
		} else if (this.nodeId != -1 && this.nodeId != destId) {
			LOGGER.error("Received message not target to progression server: " + message.toString());
			return false;
		}
		
		return true;
	}
	
	public int getNodeId() {
		return this.getNodeId();
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
		LOGGER.info("Received Long Address from gateway: " + longAddress);
	}
	
	/**
	 * Handle WKPF message.
	 *   byte 0      : command
	 *	 byte 1, 2   : seqnr
	 *	 bytes 3+    : payload
	 * @param message WKPF Message
	 */
	private void processFWDMessage(ByteBuffer message, int length) {
		if (length >= 9) {
			// Need to be used to verify correctness of message
			int destLongId = message.getInt();
			int sourceLongId = message.getInt();
			byte type = message.get();
			if (type == MPTNUtil.MPTN_MSATYPE_FWDREQ) {
				byte[] payload = new byte[message.remaining()];
				message.get(payload);
				switch(payload[0]) {
					case WKPFUtil.WKPF_REPRG_OPEN:
						fireWKPFRemoteProgramOpen(payload);
						break;
					case WKPFUtil.WKPF_REPRG_WRITE:
						fireWKPFRemoteProgramWrite(payload);
						break;
					case WKPFUtil.WKPF_REPRG_COMMIT:
						fireWKPFRemoteProgramCommit(payload);
						break;
					case WKPFUtil.WKPF_GET_WUCLASS_LIST:
						fireWKPFGetWuClassList(payload);
						break;
					case WKPFUtil.WKPF_GET_WUOBJECT_LIST:
						fireWKPFGetWuObjectList(payload);
						break;
					case WKPFUtil.WKPF_READ_PROPERTY:
						fireWKPFReadProperty(payload);
						break;
					case WKPFUtil.WKPF_WRITE_PROPERTY:
						fireWKPFWriteProperty(payload);
						break;
					case WKPFUtil.WKPF_GET_LOCATION:
						fireWKPFGetLocation(payload);
						break;
					case WKPFUtil.WKPF_SET_LOCATION:
						fireWKPFSetLocation(payload);
						break;
					case WKPFUtil.MONITORING:
						fireWKPFMonitoredData(payload);
						break;
					default:
						LOGGER.error("Received unpexcted WKPF message type " + payload[0]);
				}
			} else if (type == MPTNUtil.MPTN_MSQTYPE_IDACK) {
				processIDMessage(destLongId);
			} 
		} else {
			LOGGER.error("Received unpexcted WKPF message with length less than 9 bytes");
		}
	}
	
	private void fireWKPFRemoteProgramOpen(byte[] message) {
		LOGGER.debug("Received remote programming open message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgramOpen(message);
		}
	}
	
	private void fireWKPFRemoteProgramWrite(byte[] message) {
		LOGGER.debug("Received remote programming write message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgramWrite(message);
		}
	}
	
	private void fireWKPFRemoteProgramCommit(byte[] message) {
		LOGGER.debug("Received remote programming commit message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgramCommit(message);
		}
	}
	
	private void fireWKPFGetWuClassList(byte[] message) {
		LOGGER.info("Received get Wuclass List message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetWuClassList(message);
		}
	}
	
	private void fireWKPFGetWuObjectList(byte[] message) {
		LOGGER.debug("Received get WuObject List message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetWuObjectList(message);
		}
	}
	
	private void fireWKPFReadProperty(byte[] message) {
		LOGGER.debug("Received read Property message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFReadProperty(message);
		}
	}
	
	private void fireWKPFWriteProperty(byte[] message) {
		LOGGER.debug("Received write Property message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFWriteProperty(message);
		}
	}
	
	private void fireWKPFMonitoredData(byte[] message) {
		LOGGER.debug("Received Monitored data message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFMonitoredData(message);
		}
	}
	
	private void fireWKPFGetLocation(byte[] message) {
		LOGGER.debug("Received get location message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetLocation(message);
		}
	}
	
	private void fireWKPFSetLocation(byte[] message) {
		LOGGER.debug("Received set location message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFSetLocation(message);
		}
	}
}