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
	/* 4 unsigned char*/
	private long nounce;
	/* 8 unsigned long*/
	private int length;
	/* package paylod */
	private byte[] payload;
	
	public TCPMPTNPacket() {
		
	}
	
	public TCPMPTNPacket(int peerId, long nounce, int length, byte[] payload) {
		this.peerId = peerId;
		this.nounce = nounce;
		this.length = length;
		this.payload = payload;
	}

	@Override
	public AbstractMPTNPacket parse(ByteBuffer buffer) {
		return this;
	}
	
	public int getPeerId() {
		return peerId;
	}
	
	public int getLength() {
		return length;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public long getNounce() {
		return this.nounce;
	}
}
