package edu.uci.eecs.wukong.framework.wkpf;

import io.netty.channel.socket.DatagramChannel;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.nio.NIOUdpClient;
import edu.uci.eecs.wukong.framework.nio.NIOUdpServer;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class MPTN implements MPTNMessageListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(MPTN.class);
	private final static Configuration configuration = Configuration.getInstance();
	private final static int MPTN_ID_LEN = 4;
	private final static int MPTN_MASTER_ID = 0;
	private final static int MPTN_MAX_ID = 2 ^ (MPTN_ID_LEN * 8) - 1;
	private final static int MPTN_MSGTYPE_LEN  = 1;
	private final static int MPTN_DEST_BYTE_OFFSET = 0;
	private final static int MPTN_SRC_BYTE_OFFSET = MPTN_DEST_BYTE_OFFSET + MPTN_ID_LEN;
	private final static int MPTN_MSATYPE_BYTE_OFFSET = MPTN_SRC_BYTE_OFFSET + MPTN_ID_LEN;
	private final static int MPTN_PAYLOAD_BYTE_OFFSET = MPTN_MSATYPE_BYTE_OFFSET + MPTN_MSGTYPE_LEN;
	
	// Message types of multiple protocol transmission network in wukong
	private final static int MPTN_MSQTYPE_GWDISCOVER = 0;
	private final static int MPTN_MSQTYPE_GWOFFER = 1;
	private final static int MPTN_MSQTYPE_IDREQ = 2;
	private final static int MPTN_MSQTYPE_IDACK = 3;
	private final static int MPTN_MSQTYPE_IDNAK = 4;
	private final static int MPTN_MSQTYPE_GWIDREQ = 5;
	private final static int MPTN_MSQTYPE_GWIDACK = 6;
	private final static int MPTN_MSQTYPE_GWIDNAK = 7;
	
	private final static int MPTN_MSGTYPE_RTPING = 8;
	private final static int MPTN_MSQTYPE_RTREQ = 9;
	private final static int MPTN_MSQTYPE_RTREP = 10;
	
	private final static int MPTN_MSQTYPE_RPCCMD = 16;
	private final static int MPTN_MSQTYPE_RPCREP = 17;
	
	private final static int MPTN_MSATYPE_FWDREQ = 24;
	private final static int MPTN_MSQTYPE_TWDACK = 25;
	private final static int MPTN_MSGTYPE_FWDNAK = 26;
	private final static String HEADER_FORMAT_STR = "!IIB";
	private final static byte[] MPTN_MESSAGE_HEADER = {(byte) 0xAA, 0x55};
	
	private NIOUdpClient gatewayClient;
	private NIOUdpServer server;
	// Address for communication between IP device with gateway
	private String shortAddress; 
	// Address for communication between IP device in network
	private String longAddress;
	
	private List<WKPFMessageListener> listeners;
	
	public MPTN() {
		
		listeners = new ArrayList<WKPFMessageListener>();
		
		try {
			this.gatewayClient = new NIOUdpClient(
					configuration.getGatewayIP(), configuration.getGatewayPort());
			this.server = new NIOUdpServer();
			this.server.addMPTNMessageListener(this);
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}
	
	public void addWKPFMessageListener(WKPFMessageListener listener) {
		this.listeners.add(listener);
	}
	
	public synchronized String info() {
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		buffer.put(MPTN_MESSAGE_HEADER);
		buffer.put((byte)0);
		buffer.putLong(IPToLong(configuration.getGatewayIP()));
		buffer.putShort(configuration.getGatewayPort());
		buffer.put((byte)2);
		byte[] result = this.gatewayClient.send(buffer.array());
		return new String(result);
	}
	
	public synchronized void acquireID() {
		
	}

	public void onMessage(DatagramChannel channel, byte[] message) {
	
		
	}
	
	private long IPToLong(String ipstr) {
		long result = 0;
		String[] addresses =  ipstr.split("\\.");
		for (int i=3; i >=0; i++) {
			long ip = Long.parseLong(addresses[i]);
			result |=ip << (i * 8);
		}
		
		return result;
	}
}
