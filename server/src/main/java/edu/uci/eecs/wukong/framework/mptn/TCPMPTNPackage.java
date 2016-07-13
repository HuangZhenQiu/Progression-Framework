package edu.uci.eecs.wukong.framework.mptn;

import java.nio.ByteBuffer;

import edu.uci.eecs.wukong.framework.mptn.packet.AbstractMPTNPacket;

/**
 * TCP MPTN Package format used for communication between master and gateway, gateway and gateway
 * 
 * @author peter
 *
 */
public class TCPMPTNPackage extends AbstractMPTNPacket {
	private int peerId;
	/* 8 unsigned char*/
	private String nounce;
	/* 8 unsigned long*/
	private long length;
	/* package paylod */
	private byte[] payload;
	
	public TCPMPTNPackage() {
		
	}
	
	public TCPMPTNPackage(int peerId, String nounce, long length, byte[] payload) {
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
