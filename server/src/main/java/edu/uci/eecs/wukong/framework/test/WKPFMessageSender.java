package edu.uci.eecs.wukong.framework.test;

import edu.uci.eecs.wukong.framework.mptn.MPTNPackage;
import edu.uci.eecs.wukong.framework.mptn.UDPMPTN;
import edu.uci.eecs.wukong.framework.nio.NIOUdpClient;
import edu.uci.eecs.wukong.framework.property.Location;
import edu.uci.eecs.wukong.framework.property.Activity;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;

import java.util.Map;
import java.util.HashMap;
import java.net.Inet4Address;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * A wrapper of NIOUDPClient. It provides some functions to generate 
 * WKPF messages as input to have integration test with progression server.
 *
 */
public class WKPFMessageSender {
	private final static Logger LOGGER = LoggerFactory.getLogger(WKPFMessageSender.class);
	private final static Configuration configuration = Configuration.getInstance();	
	private NIOUdpClient client;
	/* TODO create a timer based timeout deleter*/
	private Map<Short, PackageHolder> queue;
	private PerformanceCollector collector;
	private short sequence = 1;
	private byte nodeId;
	private int sourceAddress;
	private int destAddress;
	
	public static class PackageHolder {
		private boolean ready;
		private long sendTime;
		private MPTNPackage wkpfPackage;
		
		public PackageHolder(long time) {
			this.ready = false;
			this.sendTime = time;
		}
		
		public void setPackge(MPTNPackage wkpfPackage) {
			this.wkpfPackage = wkpfPackage;
		}
		
		public void setReady(boolean ready) {
			this.ready = ready;
		}
		
		public boolean isReady() {
			return this.ready;
		}
	}
	
	
	public WKPFMessageSender(PerformanceCollector collector, String domain, int port, byte nodeId, int longAddress) {
		try {
			this.collector = collector;
			this.client = new NIOUdpClient(domain, port);
			this.queue = new HashMap<Short, PackageHolder> ();
			this.nodeId = nodeId;
			this.sourceAddress = ByteBuffer.wrap(Inet4Address.getLocalHost().getAddress()).getInt();
			this.destAddress = longAddress;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}
	
	public synchronized boolean updatePackage(MPTNPackage wkpfPackage) {
		Short sequence = wkpfPackage.getSequence();
		if (queue.get(sequence) != null) {
			queue.get(sequence).wkpfPackage = wkpfPackage;
			queue.get(sequence).ready = true;
			return true;
		}
		
		return false;
	}
	
	public void sendWriteByteProperty(byte port, short wuClassId, byte propertyId, byte value, boolean collect) {
		this.sequence++;
		ByteBuffer buffer = ByteBuffer.allocate(6); // include dummy piggyback count 0
		appendMetaData(buffer, port, wuClassId, propertyId);
		buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_REFRESH_RATE);
		buffer.put(value);
		if (collect) {
			collector.send(port, (long)this.sequence);
		}
		send(UDPMPTN.HEADER_TYPE_1, UDPMPTN.MPTN_MSQTYPE_FWDREQ, buffer.array());
	}
	
	public void sendWriteLocationProperty(byte port, short wuClassId, byte propertyId, Location location, boolean collect) {
		this.sequence++;
		ByteBuffer buffer = ByteBuffer.allocate(17); // include dummy piggyback count 0
		appendMetaData(buffer, port, wuClassId, propertyId);
		buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_LOCATION);
		buffer.putFloat(location.getX());
		buffer.putFloat(location.getY());
		buffer.putFloat(location.getZ());
		send(UDPMPTN.HEADER_TYPE_1, WKPFUtil.WKPF_WRITE_PROPERTY, buffer.array());
	}
	
	public void sendWriteActivityProperty(byte port, short wuClassId, byte propertyId, Activity activity, boolean collect) {
		this.sequence++;
		ByteBuffer buffer = ByteBuffer.allocate(23);
		appendMetaData(buffer, port, wuClassId, propertyId);
		buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_LOCATION);
		buffer.putLong(activity.getTimeStamp());
		buffer.putShort(activity.getDeviceId());
		buffer.putDouble(activity.getValue());
		send(UDPMPTN.HEADER_TYPE_1, WKPFUtil.WKPF_WRITE_PROPERTY, buffer.array());
	}
	
	public void sendWriteShortProperty(byte port, short wuClassId, byte propertyId, short value, boolean collect) {
		this.sequence++;
		ByteBuffer buffer = ByteBuffer.allocate(7); // include dummy piggyback count 0
		appendMetaData(buffer, port, wuClassId, propertyId);
		buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_SHORT);
		buffer.putShort(value);
		if (collect) {
			collector.send(port, (long)this.sequence);
		}
		send(UDPMPTN.HEADER_TYPE_1, WKPFUtil.WKPF_WRITE_PROPERTY, buffer.array());
	}
	
	
	private void appendMetaData(ByteBuffer buffer, byte port, short wuClassId, byte propertyId) {
		buffer.put(port);
		buffer.put((byte) (wuClassId % 256));
		buffer.put((byte) (wuClassId / 256));
		buffer.put(propertyId);
	}
	
	public void reprogram(byte[] payload) {
		if (payload.length == 0) {
			LOGGER.error("Stop to send empty infusion file");
			return;
		}
		
		int chunkSize = WKPFUtil.DEFAULT_WKCOMM_MESSAGE_PAYLOAD_SIZE - 2 /* 2 bytes for the position*/;
		
		LOGGER.info("Sending REPRG_OPEN cammnd wiht image size " + payload.length);
		ByteBuffer request = ByteBuffer.allocate(2);
		request.put((byte) (payload.length % 256));
		request.put((byte) (payload.length / 256));
		MPTNPackage reply = sendWaitResponse(UDPMPTN.HEADER_TYPE_1, WKPFUtil.WKPF_REPRG_OPEN, request.array());
		if (reply == null) {
			LOGGER.error("No reply from node to REPRG_OPEN command");
			return;
		}
		
		if (reply.getPayload()[2] != WKPFUtil.WKPF_REPROG_OK) {
			LOGGER.error("Got error in response to REPRG_OPEN command");
			return;
		}
		
		int pageSize = reply.getPayload()[4] + reply.getPayload()[5] * 256;
		LOGGER.info("Uploading " + payload.length + " bytes");
		short pos = 0;
		int length = 0;
		while (pos != payload.length) {
			request = ByteBuffer.allocate(WKPFUtil.DEFAULT_WKCOMM_MESSAGE_PAYLOAD_SIZE);
			request.put((byte) (pos % 256));
			request.put((byte) (pos / 256));
			length = payload.length - pos >= chunkSize? chunkSize : payload.length - pos;
			request.put(payload, pos, length);
			byte[] reqPayload = request.array();
			LOGGER.info("Uploading bytes " + pos + " to " + (pos + length) + " of " + payload.length);
			
			if ((pos / pageSize) == ((pos + reqPayload.length) / pageSize)) {
				send(UDPMPTN.HEADER_TYPE_1, WKPFUtil.WKPF_REPRG_WRITE, reqPayload);
				pos += length;
			} else {
				reply = sendWaitResponse(UDPMPTN.HEADER_TYPE_1, WKPFUtil.WKPF_REPRG_WRITE, reqPayload);
				if (reply == null) {
					LOGGER.info("No reply received. Code updade failed.");
					return;
				} else if (reply.getPayload()[2] == WKPFUtil.WKPF_REPROG_OK) {
					LOGGER.info("Received WKPF_REPROG_OK in reply to packe writting at " + pos);
					pos += length; 
				} else if (reply.getPayload()[2] == WKPFUtil.WKPF_REPROG_REQUEST_RETRANSIMIT) {
					pos = (short) (reply.getPayload()[3] + reply.getPayload()[4] * 256);
				} else {
					LOGGER.error("Received unexpected reply for repogram");
					return;
				}
			}
			
			if (pos == payload.length) {
				request = ByteBuffer.allocate(2);
				request.put((byte) (pos % 256));
				request.put((byte) (pos / 256));
				LOGGER.info("Send reprogram commit after laster packet");
				reply = sendWaitResponse(UDPMPTN.HEADER_TYPE_1, WKPFUtil.WKPF_REPRG_COMMIT, request.array());
				if (reply == null) {
					LOGGER.error("No reply, commit failed.");
					return;
				} else if (reply.getPayload()[2] == WKPFUtil.WKPF_REPROG_FAILED) {
					LOGGER.error("Received reprogram commit failed.");
					return;
				} else if (reply.getPayload()[2] == WKPFUtil.WKPF_REPROG_REQUEST_RETRANSIMIT) {
					pos = (short) (reply.getPayload()[3] + reply.getPayload()[4] * 256);
					if (pos > payload.length) {
						LOGGER.error("Recevied Reprogram request retransmit >= the image size. This shouldn't happen!");
					}
				} else if (reply.getPayload()[2] == WKPFUtil.WKPF_REPROG_OK) {
					LOGGER.info("Commit OK");
				} else {
					LOGGER.info("Unexpected reply message.");
					return;
				}
			}
			
		}
		
		request = ByteBuffer.allocate(1);
		request.put(WKPFUtil.WKPF_REPRG_REBOOT);
		send(UDPMPTN.HEADER_TYPE_1, WKPFUtil.WKPF_REPRG_REBOOT, null);
		LOGGER.info("Sent reboot");
		return;
	}

	
	public void sendNodeId(int destId, byte nodeId) {
		ByteBuffer buffer = ByteBuffer.allocate(15);
		appendMPTNHeader(buffer, destId, nodeId, UDPMPTN.HEADER_TYPE_2, (byte)1);
		buffer.put(nodeId);
		client.send(buffer.array());
	}
	
	public void sendLongAddress(byte headerType, byte[] payload) {
		client.send(pack(headerType, MPTNUtil.MPTN_MSQTYPE_IDACK, (byte)0, sequence++, payload));
	}
	
	public MPTNPackage sendWaitResponse(byte headerType, byte mptnType, byte[] payload) {
		PackageHolder holder = new PackageHolder(System.currentTimeMillis());
		queue.put(sequence, holder);
		client.send(pack(headerType, MPTNUtil.MPTN_MSGTYPE_FWDREQ, mptnType, sequence, payload));
		sequence++;
		try {
			
			while(!holder.isReady()) {
				Thread.sleep(1000);
			}
			
			LOGGER.info("Receive async response from client");
			MPTNPackage wkpfPackage = holder.wkpfPackage;
			queue.remove(wkpfPackage.getSequence());
			return wkpfPackage;
		} catch (Exception e) {
			LOGGER.info("Fail to receive response from client");
		}
		
		return null;
	}
	
	/**
	 * 
	 * Pack the MPTN message
	 * 
	 * @param destAddress
	 * @param headerType
	 * @param messageType
	 * @param payload
	 */
	public void send(byte headerType, byte messageType, byte[] payload) {
		client.send(pack(headerType, MPTNUtil.MPTN_MSGTYPE_FWDREQ, messageType, sequence++, payload));
	}
	
	private byte[] pack(byte headerType, byte mptnMessageType, byte wkpfMessageType, short sequence, byte[] payload) {
		int payloadLength = 3 /* type + sequence*/;
		if (payload != null) {
			payloadLength += payload.length;
		}
		ByteBuffer wkpfPayload = ByteBuffer.allocate(payloadLength);
		wkpfPayload.put(wkpfMessageType);
		wkpfPayload.put((byte) (sequence % 256));
		wkpfPayload.put((byte) (sequence / 256));
		if (payload!= null) {
			wkpfPayload.put(payload);
		}
		int size = payloadLength + 20;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		appendMPTNHeader(buffer, destAddress, (byte)nodeId, headerType, (byte)(payloadLength + 9));
		WKPFUtil.appendWKPFPacket(buffer, MPTNUtil.MPTN_MASTER_ID, destAddress,
				mptnMessageType, wkpfPayload.array());
		return buffer.array();
	}
	
	private void appendMPTNHeader(ByteBuffer buffer, int destAddress, byte nodeId, byte type, byte payload_bytes) {
		short port = configuration.getProgressionServerPort();
		MPTNUtil.appendMPTNHeader(buffer, destAddress, port, nodeId, type, payload_bytes);
	}
	
	
	public static void main(String[] arg) {
		WKPFMessageSender generator = new WKPFMessageSender(new PerformanceCollector(), "127.0.0.1", 9000, (byte)2, 2130706434);
		while(true) {
			try {
				generator.sendWriteShortProperty((byte)13, (short)10112, (byte)1, (short)1, false);
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println(e.toString());
			}
		}
	}
}
