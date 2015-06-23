package edu.uci.eecs.wukong.framework.util;

import java.nio.ByteBuffer;

public class MPTNUtil {
	
	public final static int MPTN_ID_LEN = 4;
	public final static int MPTN_MASTER_ID = 0;
	public final static int MPTN_MAX_ID = 2 ^ (MPTN_ID_LEN * 8) - 1;
	public final static int MPTN_MSGTYPE_LEN  = 1;
	public final static int MPTN_DEST_BYTE_OFFSET = 0;
	public final static int MPTN_SRC_BYTE_OFFSET = MPTN_DEST_BYTE_OFFSET + MPTN_ID_LEN;
	public final static int MPTN_MSATYPE_BYTE_OFFSET = MPTN_SRC_BYTE_OFFSET + MPTN_ID_LEN;
	public final static int MPTN_PAYLOAD_BYTE_OFFSET = MPTN_MSATYPE_BYTE_OFFSET + MPTN_MSGTYPE_LEN;
	
	// Message types of multiple protocol transmission network in wukong
	public final static byte MPTN_MSQTYPE_GWDISCOVER = 0;
	public final static byte MPTN_MSQTYPE_GWOFFER = 1;
	public final static byte MPTN_MSQTYPE_IDREQ = 2;
	public final static byte MPTN_MSQTYPE_IDACK = 3;
	public final static byte MPTN_MSQTYPE_IDNAK = 4;	
	public final static byte MPTN_MSATYPE_FWDREQ = 24;

	public static void appendMPTNPacket(ByteBuffer buffer, int sourceId, int destId, byte type, byte[] payload) {
		appendReversedInt(buffer, destId);
		appendReversedInt(buffer, sourceId);
		buffer.put(type);
		buffer.put(payload);
	}
	
	public static void appendReversedInt(ByteBuffer buffer, int value) {
		buffer.put(getByteValue(value, 24));
		buffer.put(getByteValue(value, 16));
		buffer.put(getByteValue(value, 8));
		buffer.put(getByteValue(value, 0));
	}
	
	public static void appendMPTNHeader(ByteBuffer buffer, int ipaddress, short port, int nodeId, byte type) {
		buffer.put((byte)0xAA);
		buffer.put((byte)0x55);
		buffer.put(getByteValue(nodeId, 0));
		appendReversedInt(buffer, ipaddress);
		buffer.put(new Integer(port%256).byteValue());
		buffer.put(new Integer(port/256).byteValue());
		buffer.put(type);
	}
	
	public static int IPToInteger(String ipstr) {
		int result = 0;
		String[] addresses =  ipstr.split("\\.");
		for (int i=3; i >=0; i--) {
			int ip = Integer.parseInt(addresses[i]);
			result |=ip << (i * 8);
		}
		
		return result;
	}
	
	private static byte getByteValue(int value, int deviation) {
		return new Integer(value >> deviation).byteValue();
	}
}
