package edu.uci.eecs.wukong.framework.test;

import edu.uci.eecs.wukong.framework.nio.NIOUdpClient;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;
import edu.uci.eecs.wukong.framework.wkpf.MPTN;

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
		send(nodeId, buffer.array());
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
		send(nodeId, buffer.array());
	}
	
	public void send(int destId, byte[] payload) {
		int size = payload.length + 20;
		ByteBuffer buffer = ByteBuffer.allocate(size);
		appendMPTNHeader(buffer, nodeId, MPTN.HEADER_TYPE_1, (byte)(payload.length + 9));
		MPTNUtil.appendMPTNPacket(buffer, longAddress, destId,
				MPTNUtil.MPTN_MSATYPE_FWDREQ, payload);
		client.send(buffer.array());
	}
	
	private void appendMPTNHeader(ByteBuffer buffer, int nodeId, byte type, byte payload_bytes) {
		int ipaddress = MPTNUtil.IPToInteger(configuration.getProgressionServerIP());
		short port = configuration.getProgressionServerPort();
		MPTNUtil.appendMPTNHeader(buffer, ipaddress, port, nodeId, type, payload_bytes);
	}
	
	
	public static void main(String[] arg) {
		WKPFMessageSender generator = new WKPFMessageSender("127.0.0.1", 9000, 2, 2130706434);
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
