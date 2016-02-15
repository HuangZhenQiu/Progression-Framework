package edu.uci.eecs.wukong.framework.mptn;

import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.MPTNPackage;
import edu.uci.eecs.wukong.framework.model.StateModel;
import edu.uci.eecs.wukong.framework.model.WKPFPackage;
import edu.uci.eecs.wukong.framework.nio.NIOUdpClient;
import edu.uci.eecs.wukong.framework.nio.NIOUdpServer;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;
import edu.uci.eecs.wukong.framework.wkpf.WKPFMessageListener;

import com.google.common.annotations.VisibleForTesting;

public class MPTN implements MPTNMessageListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(MPTN.class);
	private final static Configuration configuration = Configuration.getInstance();	
	private NIOUdpClient gatewayClient;
	private NIOUdpServer server;
	private boolean hasNodeId;
	// Address for communication between IP device with gateway
	private int nodeId = -1; 
	// Address for communication between IP device in network
	private long longAddress;
	// local ip address
	private String serverAddress;
	// local ip integer
	private int serverIP;
	// UUID for node id acquire
	private byte[] uuid;
	
	private List<WKPFMessageListener> listeners;
	public static int MPTN_HEADER_LENGTH = 9;
	public static byte HEADER_TYPE_1 = 1;
	public static byte HEADER_TYPE_2 = 2;
	
	public static int MPTN_ID_LEN = 4;
	public static int MPTN_MAX_ID = 2 ^ (MPTN_ID_LEN * 8) - 1;
	public static int MPTN_MSQTYPE_LEN = 1;
	
	public static int CONNECTION_RETRIES = 1;
	public static int NETWORK_TIMEOUT = 3;
	
	// transport interface sub network length
	public static int ZW_ADDRESS_LEN = 1;
	public static int ZB_ADDRESS_LEN = 2;
	public static int IP_ADDRESS_LEN = 4;
	
	// MODE for adding and deleting node
	public static int STOP_MODE = 0;
	public static int ADD_MODE = 1;
	public static int DEL_MODE = 2;
	
	// protocol handler admission control
	public static int ONLY_FROM_TCP_SERVER = 1; // Master or peer gateway
	public static int ONLY_FROM_TRANSPORT_INTERFACE = 2;
	public static int VALID_FROM_ALL = 3;
	
	// ID service
	public static byte MPTN_MSGTYPE_GWDISCOVER = 0;
	public static byte MPTN_MSGTYPE_GWOFFER = 1;
	public static byte MPTN_MSGTYPE_IDREQ = 2;
	public static byte MPTN_MSGTYPE_IDACK = 3;
	public static byte MPTN_MSGTYPE_IDNAK = 4;
	public static byte MPTN_MSGTYPE_GWIDREQ = 5;
	public static byte MPTN_MSGTYPE_GWIDACK = 6;
	public static byte MPTN_MSGTYPE_GWIDNAK = 7;
	
    // Heartbeat
	public static byte MPTN_MSGTYPE_RTPING = 8;
	public static byte MPTN_MSGTYPE_RTREQ = 9;
	public static byte MPTN_MSGTYPE_RTREP = 10;
	
	// RPC service with master
	public static byte MPTN_MSQTYPE_RPCCMD = 16;
	public static byte MPTN_MSQTYPE_RPCREP = 17;
	
	// Message forward
	public static byte MPTN_MSQTYPE_FWDREQ = 24;
	public static byte MPTN_MSQTYPE_FWDACK = 25;
	public static byte MPTN_MSQTYPE_FWDNAK = 26;
	
	public int MASTER_ID = 0;
	public int MPTN_UDP_PORT = 5775;
	
	public int GWIDREQ_PAYLOAD_LEN = 16;
	public int IDREQ_PAYLOAD_LEN = 16;
	
	public MPTN() {
		
		listeners = new ArrayList<WKPFMessageListener>();
		this.hasNodeId = false;
		try {
			if (configuration.getProgressionServerIP() != null) {
				this.serverAddress = configuration.getProgressionServerIP();
			} else {
				this.serverAddress = InetAddress.getLocalHost().getHostAddress();
			}
			this.serverIP = MPTNUtil.IPToInteger(this.serverAddress);
			LOGGER.info("Starting progression server MPTN at ip address " + this.serverAddress);
			this.gatewayClient = new NIOUdpClient(
					configuration.getGatewayIP(), configuration.getGatewayPort());
			this.server = new NIOUdpServer(configuration.getProgressionServerPort());
			this.server.addMPTNMessageListener(this);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e.toString());
		}
	}
	
	public void start(StateModel model) {
		try {
			Thread serverThread = new Thread(server);
			serverThread.start();
			Thread.sleep(1000);
			if (model == null || model.getNodeId() == null
					|| model.getLongAddress() == null || model.getUuid() == null) {
				info();
				while (!hasNodeId) {
					Thread.sleep(1000);
				}
				acquireID();
			} else {
				this.nodeId = model.getNodeId();
				this.longAddress = model.getLongAddress();
				this.uuid = model.getUuid().getBytes();
			}
			this.hasNodeId = true;
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Fail to start MPTN");
		}
	}
	
	public void shutdown() {
		gatewayClient.shutdown();
		server.shutdown();
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
		if (this.uuid == null) {
			this.uuid = generateUUID();
		}
		WKPFUtil.appendWKPFPacket(buffer, MPTNUtil.MPTN_MAX_ID.intValue(), MPTNUtil.MPTN_MASTER_ID,
				MPTNUtil.MPTN_MSQTYPE_IDREQ, this.uuid);
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
	public void send(long destId, byte[] payload) {
		int size = payload.length + 20;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		appendMPTNHeader(buffer, nodeId, HEADER_TYPE_1, (byte)(payload.length + 9));
		WKPFUtil.appendWKPFPacket(buffer, longAddress, destId,
				MPTNUtil.MPTN_MSATYPE_FWDREQ, payload);
		LOGGER.info(MPTNUtil.toHexString(buffer.array()));
		gatewayClient.send(buffer.array());
	}
	
	private void appendMPTNHeader(ByteBuffer buffer, int nodeId, byte type, byte payload_bytes) {
		short port = configuration.getProgressionServerPort();
		MPTNUtil.appendMPTNHeader(buffer, serverIP, port, nodeId, type, payload_bytes);
	}

	public void onMessage(MPTNPackage message) {
		if (validateMPTNHeader(message)) {
			if (message.getType() == HEADER_TYPE_1) { 
				processFWDMessage(message);
			} else if (message.getType() == HEADER_TYPE_2){
				if (message.getLength() == 1) {
					processInfoMessage(message.getPayload()[0]);
				}
			} else {
				LOGGER.error("Received message error message type");
			}
		}
	}
	
	private boolean validateMPTNHeader(MPTNPackage message) {
		if (message.getH1() != 0xAA || message.getH2() != 0x55) {
			LOGGER.error("Received Incorrect Wukong Message.");
			return false;
		} else if (this.nodeId != -1 && this.nodeId != message.getNodeId()) {
			LOGGER.error("Received message not target to progression server: " + message.toString());
			return false;
		}
		
		return true;
	}
	
	/**
	 * The local network Id
	 * @return
	 */
	public int getNodeId() {
		return this.nodeId;
	}
	
	/**
	 * The global network Id address. Int value of four bytes address.
	 * @return
	 */
	public long getLongAddress() {
		return this.longAddress;
	}
	
	/**
	 * Handle the response for INFO response.
	 * 
	 * @param message
	 */
	private void processInfoMessage(int nodeId) {
		this.nodeId = nodeId;
		this.hasNodeId = true;
		LOGGER.info("Recevied Node Id: " + nodeId);
	}

	
	
	/**
	 * Handle the response for ID acquire.
	 * 
	 * @param message
	 */
	private void processIDMessage(long address) {
		this.longAddress = address;
		LOGGER.info("Received Long Address from gateway: " + longAddress);
	}
	
	/**
	 * Handle WKPF message.
	 *   byte 0      : command
	 *	 byte 1, 2   : seqnr
	 *	 bytes 3+    : payload
	 * @param message WKPF Message
	 */
	@VisibleForTesting
	public byte processFWDMessage(MPTNPackage message) {
		if (message.getLength() >= 9) {
			WKPFPackage wkpfPackage = new WKPFPackage(message.getPayload());
			if (wkpfPackage.getType() == MPTNUtil.MPTN_MSATYPE_FWDREQ) {
				switch(wkpfPackage.getPayload()[0]) {
					case WKPFUtil.WKPF_REPRG_OPEN:
						fireWKPFRemoteProgramOpen(wkpfPackage);
						return WKPFUtil.WKPF_REPRG_OPEN;
					case WKPFUtil.WKPF_REPRG_WRITE:
						fireWKPFRemoteProgramWrite(wkpfPackage);
						return WKPFUtil.WKPF_REPRG_WRITE;
					case WKPFUtil.WKPF_REPRG_COMMIT:
						fireWKPFRemoteProgramCommit(wkpfPackage);
						return WKPFUtil.WKPF_REPRG_COMMIT;
					case WKPFUtil.WKPF_GET_WUCLASS_LIST:
						fireWKPFGetWuClassList(wkpfPackage);
						return WKPFUtil.WKPF_GET_WUCLASS_LIST;
					case WKPFUtil.WKPF_GET_WUOBJECT_LIST:
						fireWKPFGetWuObjectList(wkpfPackage);
						return WKPFUtil.WKPF_GET_WUOBJECT_LIST;
					case WKPFUtil.WKPF_READ_PROPERTY:
						fireWKPFReadProperty(wkpfPackage);
						return WKPFUtil.WKPF_READ_PROPERTY;
					case WKPFUtil.WKPF_WRITE_PROPERTY:
						fireWKPFWriteProperty(wkpfPackage);
						return WKPFUtil.WKPF_WRITE_PROPERTY;
					case WKPFUtil.WKPF_REQUEST_PROPERTY_INIT:
						fireWKPFRequestPropertyInit(wkpfPackage);
						return WKPFUtil.WKPF_REQUEST_PROPERTY_INIT;
					case WKPFUtil.WKPF_GET_LOCATION:
						fireWKPFGetLocation(wkpfPackage);
						return WKPFUtil.WKPF_GET_LOCATION;
					case WKPFUtil.WKPF_SET_LOCATION:
						fireWKPFSetLocation(wkpfPackage);
						return  WKPFUtil.WKPF_SET_LOCATION;
					case WKPFUtil.MONITORING:
						fireWKPFMonitoredData(wkpfPackage);
						return WKPFUtil.MONITORING;
					case WKPFUtil.WKPF_REPRG_REBOOT:
						LOGGER.info("I dont't want to reboot");
						return WKPFUtil.WKPF_REPRG_REBOOT;
					case WKPFUtil.WKPF_WRITE_PROPERTY_R:
						return WKPFUtil.WKPF_WRITE_PROPERTY_R;
					default:
						LOGGER.error("Received unpexcted WKPF message type " + wkpfPackage);
				}
			} else if (wkpfPackage.getType() == MPTNUtil.MPTN_MSQTYPE_IDACK) {
				processIDMessage(wkpfPackage.getDestAddress());
			} 
		} else {
			LOGGER.error("Received unpexcted WKPF message with length less than 9 bytes");
		}
		return WKPFUtil.WKPF_ERROR;
	}
	
	private void fireWKPFRemoteProgramOpen(WKPFPackage message) {
		LOGGER.debug("Received remote programming open message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgramOpen(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFRemoteProgramWrite(WKPFPackage message) {
		LOGGER.debug("Received remote programming write message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgramWrite(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFRemoteProgramCommit(WKPFPackage message) {
		LOGGER.debug("Received remote programming commit message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgramCommit(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFGetWuClassList(WKPFPackage message) {
		LOGGER.info("Received get Wuclass List message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetWuClassList(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFGetWuObjectList(WKPFPackage message) {
		LOGGER.debug("Received get WuObject List message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetWuObjectList(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFReadProperty(WKPFPackage message) {
		LOGGER.debug("Received read Property message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFReadProperty(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFWriteProperty(WKPFPackage message) {
		LOGGER.debug("Received write Property message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFWriteProperty(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFRequestPropertyInit(WKPFPackage message) {
		LOGGER.debug("Received Request Property init message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRequestPropertyInit(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFMonitoredData(WKPFPackage message) {
		LOGGER.debug("Received Monitored data message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFMonitoredData(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFGetLocation(WKPFPackage message) {
		LOGGER.debug("Received get location message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetLocation(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFSetLocation(WKPFPackage message) {
		LOGGER.debug("Received set location message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFSetLocation(message.getSourceAddress(), message.getPayload());
		}
	}

	public boolean isHasNodeId() {
		return hasNodeId;
	}

	public void setHasNodeId(boolean hasNodeId) {
		this.hasNodeId = hasNodeId;
	}

	public byte[] getUuid() {
		return uuid;
	}

	public void setUuid(byte[] uuid) {
		this.uuid = uuid;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public void setLongAddress(long longAddress) {
		this.longAddress = longAddress;
	}
}
