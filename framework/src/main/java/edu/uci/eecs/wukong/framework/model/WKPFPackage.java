package edu.uci.eecs.wukong.framework.model;

import java.util.Arrays;

import edu.uci.eecs.wukong.framework.util.WKPFUtil;

public class WKPFPackage {
	private int sourceAddress;
	private int destAddress;
	private byte type;
	private byte[] payload;
	
	public WKPFPackage(byte[] payload) {
		this.destAddress =  WKPFUtil.getBigEndianInteger(payload, 0);
		this.sourceAddress = WKPFUtil.getBigEndianInteger(payload, 4);
		this.type = payload[8];
		this.payload = Arrays.copyOfRange(payload, 9, payload.length);
	}

	public int getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(int sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public int getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(int destAddress) {
		this.destAddress = destAddress;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public short getSequence() {
		return WKPFUtil.getBigEndianShort(payload, 1);
	}
}
