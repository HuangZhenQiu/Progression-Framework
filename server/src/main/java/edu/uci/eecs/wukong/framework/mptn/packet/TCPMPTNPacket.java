package edu.uci.eecs.wukong.framework.mptn.packet;

import java.nio.ByteBuffer;

/**
 * TCP MPTN Package format used for communication between master and gateway, gateway and gateway
 * 
 * @author peter
 *
 */
public class TCPMPTNPacket extends AbstractMPTNPacket {
	private int peerId;
	/* 8 unsigned char*/
	private String nounce;
	/* 8 unsigned long*/
	private long length;
	/* package paylod */
	private byte[] payload;
	
	public TCPMPTNPacket() {
		
	}
	
	public TCPMPTNPacket(int peerId, String nounce, long length, byte[] payload) {
		this.peerId = peerId;
		this.nounce = nounce;
		this.length = length;
		this.payload = payload;
	}

	@Override
	public AbstractMPTNPacket parse(ByteBuffer buffer) {
		return this;
	}
	
	public long getLength() {
		return length;
	}
	
	public byte[] getPayload() {
		return payload;
	}
}
