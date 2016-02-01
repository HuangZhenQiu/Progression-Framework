package edu.uci.eecs.wukong.framework.model;

import java.nio.ByteBuffer;
import java.util.Arrays;

import edu.uci.eecs.wukong.framework.mptn.MPTN;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;

public class MPTNPackage {
	private int h1;
	private int h2;
	private byte nodeId;
	private int sourceIP;
	private byte[] sourceIPBytes;
	private short soucePort;
	private byte type;
	private int length;
	private byte[] payload;
	
	public MPTNPackage(ByteBuffer buffer) throws Exception {
		byte[] header = new byte[MPTN.MPTN_HEADER_LENGTH];
		buffer.get(header);
		this.h1 = header[0] & 0xFF;
		this.h2 = header[1] & 0xFF;
		this.nodeId = header[2];
		this.sourceIP = WKPFUtil.getLittleEndianInteger(header, 3);
		this.sourceIPBytes = WKPFUtil.getLittleEndianIntegerBytes(header, 3);
		this.soucePort = WKPFUtil.getLittleEndianShort(header, 7);
		this.type = buffer.get();
		this.length = (int) buffer.get();
		this.payload = new byte[length];
		buffer.get(payload);
	}

	public int getH1() {
		return h1;
	}

	public void setH1(int h1) {
		this.h1 = h1;
	}

	public int getH2() {
		return h2;
	}

	public void setH2(int h2) {
		this.h2 = h2;
	}

	public byte getNodeId() {
		return nodeId;
	}

	public void setNodeId(byte nodeId) {
		this.nodeId = nodeId;
	}

	public int getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(int sourceIP) {
		this.sourceIP = sourceIP;
	}

	public byte[] getSourceIPBytes() {
		return sourceIPBytes;
	}

	public void setSourceIPBytes(byte[] sourceIPBytes) {
		this.sourceIPBytes = sourceIPBytes;
	}

	public short getSoucePort() {
		return soucePort;
	}

	public void setSoucePort(short soucePort) {
		this.soucePort = soucePort;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
}
