package edu.uci.eecs.wukong.framework.wkpf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
	// Address for communication between IP device with gateway
	private int nodeId; 
	// Address for communication between IP device in network
	private String longAddress;
	
	private List<WKPFMessageListener> listeners;
	
	public MPTN() {
		
		listeners = new ArrayList<WKPFMessageListener>();
		
		try {
			this.gatewayClient = new NIOUdpClient(
					configuration.getGatewayIP(), configuration.getGatewayPort());
			this.server = new NIOUdpServer(Integer.parseInt(configuration.getProgressionServerPort()));
			this.server.addMPTNMessageListener(this);
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}
	
	public void addWKPFMessageListener(WKPFMessageListener listener) {
		this.listeners.add(listener);
	}
	
	public synchronized void acquireID() {
		
	}
	
	public void send() {
		
	}

	public void onMessage(ByteBuffer message) {
		int destId = message.getInt();
		int srcId = message.getInt();
		byte messageType = message.get();
		if (destId == nodeId) { 
			if (messageType == MPTNUtil.MPTN_MSATYPE_FWDREQ) {
				processIDMessage(message.slice());
			} else if (messageType == MPTNUtil.MPTN_MSQTYPE_IDACK) {
				processFWDMessage(message.slice());
			} else if (messageType == MPTNUtil.MPTN_MSQTYPE_GWOFFER) {
				LOGGER.info("Gateway Discovery is not implemented.");
			} else {
				LOGGER.error("Received wrong type of messages from :" +  message.toString());
			}
		} else {
			LOGGER.error("Received message not target to progression server: " + message.toString());
		}
	}
	
	
	/**
	 * Handle the response for ID acquire.
	 * 
	 * @param message
	 */
	private void processIDMessage(ByteBuffer message) {
		
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
