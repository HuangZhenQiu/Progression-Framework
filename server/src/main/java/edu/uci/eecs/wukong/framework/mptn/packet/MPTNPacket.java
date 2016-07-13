package edu.uci.eecs.wukong.framework.mptn.packet;

import java.util.Arrays;

import edu.uci.eecs.wukong.framework.util.WKPFUtil;

public class MPTNPacket {
	private long sourceAddress;
	private long destAddress;
	private byte type;
	private byte[] payload;
	
	public MPTNPacket(byte[] payload) {
		this.destAddress =  WKPFUtil.getLong(payload, 0);
		this.sourceAddress = WKPFUtil.getLong(payload, 4);
		this.type = payload[8];
		this.payload = Arrays.copyOfRange(payload, 9, payload.length);
	}

	public long getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(long sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public long getDestAddress() {
		return destAddress;
	}

	public void setDestAddress(long destAddress) {
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
