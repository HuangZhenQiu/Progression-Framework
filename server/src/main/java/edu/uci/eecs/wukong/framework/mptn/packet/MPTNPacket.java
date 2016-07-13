package edu.uci.eecs.wukong.framework.mptn.packet;

import java.nio.ByteBuffer;
import java.util.Arrays;

import edu.uci.eecs.wukong.framework.util.MPTNUtil;
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
	
	public MPTNPacket(long sourceAddress, long destAddress, byte type, byte[] payload) {
		this.sourceAddress = sourceAddress;
		this.destAddress = destAddress;
		this.type = type;
		this.payload = payload;
	}
	
	public byte[] asByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(MPTNUtil.MPTN_HEADER_LENGTH + payload.length);
		buffer.putInt((int)this.getDestAddress());
		buffer.putInt((int)this.getSourceAddress());
		buffer.put(type);
		buffer.put(payload);
		return buffer.array();
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
