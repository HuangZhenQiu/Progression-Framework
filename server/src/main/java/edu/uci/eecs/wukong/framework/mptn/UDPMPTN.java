package edu.uci.eecs.wukong.framework.mptn;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.StateModel;
import edu.uci.eecs.wukong.framework.nio.NIOUdpServer;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;
import edu.uci.eecs.wukong.framework.wkpf.WKPFMessageListener;

import com.google.common.annotations.VisibleForTesting;

/**
 * UDPMPTN will be used in both progression server and edge server.
 * 
 * In edge server, it needs to get an device id from gateway.
 * In progression server, it doesn't need to get device ID. Its default id is 1.
 * which is the default monitoring destination in master
 * 
 * 
 * @author peter
 *
 */
public class UDPMPTN extends AbstractMPTN implements MPTNMessageListener<UDPMPTNPackage> {
	private final static Logger LOGGER = LoggerFactory.getLogger(UDPMPTN.class);
	private final static Configuration configuration = Configuration.getInstance();	
	// Default Gateway Address;
	private SocketAddress defaultAddress;
	// Used for progression server, map node Id with gateway socket address
	private Map<Long, SocketAddress> idGatewayMap;
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
	// Progression or Edge
	private boolean progression;
	
	private List<WKPFMessageListener> listeners;
	
	public UDPMPTN(boolean progression) {
		this.progression = progression;
		this.listeners = new ArrayList<WKPFMessageListener>();
		this.idGatewayMap = new HashMap<Long, SocketAddress> ();
		this.defaultAddress =
				new InetSocketAddress(configuration.getGatewayIP(), configuration.getGatewayPort());
		if (!progression) {
			this.hasNodeId = false;
			try {
				if (configuration.getProgressionServerIP() != null) {
					this.serverAddress = configuration.getProgressionServerIP();
				} else {
					this.serverAddress = InetAddress.getLocalHost().getHostAddress();
				}
				this.serverIP = MPTNUtil.IPToInteger(this.serverAddress);
				LOGGER.info("Starting progression server MPTN at ip address " + this.serverAddress);
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error(e.toString());
			}
		} else {
			// If it is progression server, its node Id is 1 by default
			// So that it can receive all of the monitoring data.
			this.hasNodeId = true;
			this.nodeId = 1;
			this.longAddress = 1;
		}
		
		this.server = new NIOUdpServer(configuration.getProgressionServerPort());
		this.server.addMPTNMessageListener(this);
		Thread serverThread = new Thread(server);
		serverThread.start();
		
	}
	
	public void start(StateModel model) {
		try {
			if (!progression) {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("Fail to start MPTN");
		}
	}
	
	public void shutdown() {
		server.shutdown();
	}
	
	public void register(WKPFMessageListener listener) {
		this.listeners.add(listener);
	}
	
	public synchronized void info() {
		ByteBuffer buffer = ByteBuffer.allocate(11);
		appendMPTNHeader(buffer, MPTNUtil.MPTN_MASTER_ID, HEADER_TYPE_2, (byte)0);
		buffer.flip();
		server.send(defaultAddress, buffer);
	}
	
	public synchronized void acquireID() {
		ByteBuffer buffer = ByteBuffer.allocate(36 /* 11 + 9 + 16*/);
		appendMPTNHeader(buffer, nodeId, HEADER_TYPE_1, (byte)25);
		if (this.uuid == null) {
			this.uuid = generateUUID();
		}
		WKPFUtil.appendWKPFPacket(buffer, MPTNUtil.MPTN_MAX_ID.intValue(), MPTNUtil.MPTN_MASTER_ID,
				MPTNUtil.MPTN_MSQTYPE_IDREQ, this.uuid);
		buffer.flip();
		server.send(defaultAddress, buffer);
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
				MPTNUtil.MPTN_MSGTYPE_FWDREQ, payload);
		LOGGER.debug(MPTNUtil.toHexString(buffer.array()));
		buffer.flip();
		if (!progression) {
			// edge server send message to its connected gateway
			server.send(defaultAddress, buffer);
		} else {
			if (idGatewayMap.containsKey(destId)) {
				server.send(idGatewayMap.get(destId), buffer);
			} else {
				server.send(defaultAddress, buffer);
			}
		}
	}
	
	private void appendMPTNHeader(ByteBuffer buffer, int nodeId, byte type, byte payload_bytes) {
		short port = configuration.getProgressionServerPort();
		MPTNUtil.appendMPTNHeader(buffer, serverIP, port, nodeId, type, payload_bytes);
	}

	public void onMessage(SocketAddress remoteAddress, UDPMPTNPackage message) {
		if (validateMPTNHeader(message)) {
			if (message.getType() == HEADER_TYPE_1) { 
				processFWDMessage(remoteAddress, message);
			} else if (message.getType() == HEADER_TYPE_2){
				if (message.getLength() == 1) {
					processInfoMessage(message.getPayload()[0]);
				}
			} else {
				LOGGER.error("Received message error message type");
			}
		}
	}
	
	private boolean validateMPTNHeader(UDPMPTNPackage message) {
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
	public byte processFWDMessage(SocketAddress remoteAddress, UDPMPTNPackage message) {
		if (message.getLength() >= 9) {
			MPTNPackage wkpfPackage = new MPTNPackage(message.getPayload());
			if (!idGatewayMap.containsKey(wkpfPackage.getSourceAddress())) {
				idGatewayMap.put(wkpfPackage.getSourceAddress(), remoteAddress);
			}
			
			if (wkpfPackage.getType() == MPTNUtil.MPTN_MSGTYPE_FWDREQ) {
				switch(wkpfPackage.getPayload()[0] & 0xFF) {
					case WKPFUtil.WKPF_REPRG_OPEN & 0xFF:
						fireWKPFRemoteProgramOpen(wkpfPackage);
						return WKPFUtil.WKPF_REPRG_OPEN;
					case WKPFUtil.WKPF_REPRG_WRITE & 0xFF:
						fireWKPFRemoteProgramWrite(wkpfPackage);
						return WKPFUtil.WKPF_REPRG_WRITE;
					case WKPFUtil.WKPF_REPRG_COMMIT & 0xFF:
						fireWKPFRemoteProgramCommit(wkpfPackage);
						return WKPFUtil.WKPF_REPRG_COMMIT;
					case WKPFUtil.WKPF_GET_WUCLASS_LIST & 0xFF:
						fireWKPFGetWuClassList(wkpfPackage);
						return WKPFUtil.WKPF_GET_WUCLASS_LIST;
					case WKPFUtil.WKPF_GET_WUOBJECT_LIST & 0xFF:
						fireWKPFGetWuObjectList(wkpfPackage);
						return WKPFUtil.WKPF_GET_WUOBJECT_LIST;
					case WKPFUtil.WKPF_READ_PROPERTY & 0xFF:
						fireWKPFReadProperty(wkpfPackage);
						return WKPFUtil.WKPF_READ_PROPERTY;
					case WKPFUtil.WKPF_WRITE_PROPERTY & 0xFF:
						fireWKPFWriteProperty(wkpfPackage);
						return WKPFUtil.WKPF_WRITE_PROPERTY;
					case WKPFUtil.WKPF_REQUEST_PROPERTY_INIT & 0xFF:
						fireWKPFRequestPropertyInit(wkpfPackage);
						return WKPFUtil.WKPF_REQUEST_PROPERTY_INIT;
					case WKPFUtil.WKPF_GET_LOCATION & 0xFF:
						fireWKPFGetLocation(wkpfPackage);
						return WKPFUtil.WKPF_GET_LOCATION;
					case WKPFUtil.WKPF_SET_LOCATION & 0xFF:
						fireWKPFSetLocation(wkpfPackage);
						return WKPFUtil.WKPF_SET_LOCATION ;
					case WKPFUtil.MONITORING & 0xFF:
						fireWKPFMonitoredData(wkpfPackage);
						return WKPFUtil.MONITORING;
					case WKPFUtil.WKPF_REPRG_REBOOT & 0xFF:
						LOGGER.debug("I dont't want to reboot");
						return WKPFUtil.WKPF_REPRG_REBOOT;
					case WKPFUtil.WKPF_GET_LINK_COUNTER_R & 0xFF:
						fireWKPFOnGetLinkCounterReturn(wkpfPackage);
						return WKPFUtil.WKPF_GET_LINK_COUNTER_R;
					case WKPFUtil.WKPF_GET_DEVICE_STATUS_R & 0xFF:
						fireWKPFOnGetDeviceStatusReturn(wkpfPackage);
						return WKPFUtil.WKPF_GET_DEVICE_STATUS_R;
					case WKPFUtil.WKPF_SET_LOCK_R:
						fireWKPFOnSetLockReturn(wkpfPackage);
					case WKPFUtil.WKPF_CHANGE_LINK_R:
						fireWKPFOnChangeLinkReturn(wkpfPackage);
					case WKPFUtil.WKPF_RELEASE_LOCK_R:
						fireWKPFOnReleaseLockReturn(wkpfPackage);
						
					default:
						LOGGER.error("Received unpexcted MPTN message type " + wkpfPackage);
				}
			} else if (wkpfPackage.getType() == MPTNUtil.MPTN_MSQTYPE_IDACK) {
				processIDMessage(wkpfPackage.getDestAddress());
			} 
		} else {
			LOGGER.error("Received unpexcted MPTN message with length less than 9 bytes");
		}
		return WKPFUtil.WKPF_ERROR;
	}
	
	private void fireWKPFRemoteProgramOpen(MPTNPackage message) {
		LOGGER.debug("Received remote programming open message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgramOpen(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFRemoteProgramWrite(MPTNPackage message) {
		LOGGER.debug("Received remote programming write message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgramWrite(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFRemoteProgramCommit(MPTNPackage message) {
		LOGGER.debug("Received remote programming commit message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRemoteProgramCommit(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFGetWuClassList(MPTNPackage message) {
		LOGGER.debug("Received get Wuclass List message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetWuClassList(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFGetWuObjectList(MPTNPackage message) {
		LOGGER.debug("Received get WuObject List message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetWuObjectList(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFReadProperty(MPTNPackage message) {
		LOGGER.debug("Received read Property message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFReadProperty(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFWriteProperty(MPTNPackage message) {
		LOGGER.debug("Received write Property message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFWriteProperty(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFRequestPropertyInit(MPTNPackage message) {
		LOGGER.debug("Received Request Property init message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFRequestPropertyInit(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFMonitoredData(MPTNPackage message) {
		LOGGER.debug("Received Monitored data message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFMonitoredData(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFGetLocation(MPTNPackage message) {
		LOGGER.debug("Received get location message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFGetLocation(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFSetLocation(MPTNPackage message) {
		LOGGER.debug("Received set location message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFSetLocation(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFOnGetLinkCounterReturn(MPTNPackage message) {
		LOGGER.debug("Received get link counter message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFLinkCounterReturn(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFOnGetDeviceStatusReturn(MPTNPackage message) {
		LOGGER.debug("Received get device status message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFDeviceStatusReturn(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFOnSetLockReturn(MPTNPackage message) {
		LOGGER.debug("Received set lock return message");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFSetLockReturn(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFOnChangeLinkReturn(MPTNPackage message) {
		LOGGER.debug("Received change link return");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFChangeLinkReturn(message.getSourceAddress(), message.getPayload());
		}
	}
	
	private void fireWKPFOnReleaseLockReturn(MPTNPackage message) {
		LOGGER.debug("Received release lock return");
		for (WKPFMessageListener listener : listeners) {
			listener.onWKPFReleaseLockReturn(message.getSourceAddress(), message.getPayload());
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
