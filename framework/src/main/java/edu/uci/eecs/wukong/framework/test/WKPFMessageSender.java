package edu.uci.eecs.wukong.framework.test;

import edu.uci.eecs.wukong.framework.mptn.MPTN;
import edu.uci.eecs.wukong.framework.nio.NIOUdpClient;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;

import java.nio.ByteBuffer;

/***
 * A wrapper of NIOUDPClient. It provides some functions to generate 
 * WKPF messages as input to have integration test with progression server.
 *
 */
public class WKPFMessageSender {
	private final static Configuration configuration = Configuration.getInstance();	
	private NIOUdpClient client;
	private PerformanceCollector collector;
	private int sequence = 1;
	private int nodeId;
	private long longAddress;
	
	public WKPFMessageSender(PerformanceCollector collector, String domain, int port, int nodeId, long longAddress) {
		try {
			this.collector = collector;
			this.client = new NIOUdpClient(domain, port);
			this.nodeId = nodeId;
			this.longAddress = longAddress;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.toString());
		}
	}
	
	public void sendWriteByteProperty(byte port, short wuClassId, byte propertyId, byte value) {
		this.sequence++;
		ByteBuffer buffer = ByteBuffer.allocate(9); // include dummy piggyback count 0
		buffer.put(WKPFUtil.WKPF_WRITE_PROPERTY);
		buffer.put((byte) (this.sequence % 256));
		buffer.put((byte) (this.sequence / 256));
		buffer.put(port);
		buffer.putShort(wuClassId);
		buffer.put(propertyId);
		buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_REFRESH_RATE);
		buffer.put(value);
		send(nodeId, MPTN.HEADER_TYPE_1, MPTN.MPTN_MSQTYPE_FWDREQ, buffer.array());
	}
	
	public void sendWriteShortProperty(byte port, short wuClassId, byte propertyId, short value) {
		this.sequence++;
		ByteBuffer buffer = ByteBuffer.allocate(10); // include dummy piggyback count 0
		buffer.put(WKPFUtil.WKPF_WRITE_PROPERTY);
		buffer.put((byte) (this.sequence % 256));
		buffer.put((byte) (this.sequence / 256));
		buffer.put(port);
		buffer.putShort(wuClassId);
		buffer.put(propertyId);
		buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_SHORT);
		buffer.putShort(value);
		send(nodeId, MPTN.HEADER_TYPE_1, MPTN.MPTN_MSQTYPE_FWDREQ, buffer.array());
	}
	
	public void reprogram(byte[] payload) {
		
	}
	
	public void sendNodeId(int destId, byte nodeId) {
		ByteBuffer buffer = ByteBuffer.allocate(15);
		appendMPTNHeader(buffer, destId, nodeId, MPTN.HEADER_TYPE_2, (byte)1);
		buffer.putInt(nodeId);
		client.send(buffer.array());
	}
	
	public void send(int destAddress, byte headerType, byte messageType, byte[] payload) {
		int size = payload.length + 20;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		appendMPTNHeader(buffer, destAddress, nodeId, headerType, (byte)(payload.length + 9));
		MPTNUtil.appendMPTNPacket(buffer, longAddress, destAddress,
				messageType, payload);
		client.send(buffer.array());
	}
	
	private void appendMPTNHeader(ByteBuffer buffer, int destAddress, int nodeId, byte type, byte payload_bytes) {
		short port = configuration.getProgressionServerPort();
		MPTNUtil.appendMPTNHeader(buffer, destAddress, port, nodeId, type, payload_bytes);
	}
	
	
	public static void main(String[] arg) {
		WKPFMessageSender generator = new WKPFMessageSender(new PerformanceCollector(), "127.0.0.1", 9000, 2, 2130706434);
		while(true) {
			try {
				generator.sendWriteShortProperty((byte)13, (short)10112, (byte)1, (short)1);
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println(e.toString());
			}
		}
	}
}
