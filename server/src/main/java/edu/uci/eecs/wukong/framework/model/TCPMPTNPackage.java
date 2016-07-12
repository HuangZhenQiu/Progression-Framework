package edu.uci.eecs.wukong.framework.model;

/**
 * TCP MPTN Package format used for communication between master and gateway, gatway and gatewy
 * 
 * @author peter
 *
 */
public class TCPMPTNPackage {
	private int peerId;
	/* 8 unsigned char*/
	private String nounce;
	/* 8 unsigned long*/
	private long length;
	/* package paylod */
	private byte[] payload;
	
	public TCPMPTNPackage(int peerId, String nounce, long length, byte[] payload) {
		this.peerId = peerId;
		this.nounce = nounce;
		this.length = length;
		this.payload = payload;
	}
}
