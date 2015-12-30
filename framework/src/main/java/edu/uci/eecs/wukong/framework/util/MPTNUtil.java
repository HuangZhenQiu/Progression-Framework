package edu.uci.eecs.wukong.framework.util;

import java.nio.ByteBuffer;

import com.google.common.primitives.UnsignedInteger;
public class MPTNUtil {
	
	public final static int MPTN_ID_LEN = 4;
	public final static int MPTN_MASTER_ID = 0;
	public final static UnsignedInteger MPTN_MAX_ID =UnsignedInteger.MAX_VALUE;
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
	
	public static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	public static String toHexString(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for (int j = 0; j < bytes.length; j++) {
	      v = bytes[j] & 0xFF;
	      hexChars[j * 2] = HEX_CHARS[v >>> 4];
	      hexChars[j * 2 + 1] = HEX_CHARS[v & 0x0F];
	    }
	    return new String(hexChars);
	}

	public static void appendMPTNPacket(ByteBuffer buffer, long longAddress, int destId, byte type, byte[] payload) {
		appendReversedInt(buffer, destId);
		appendReversedInt(buffer, (int) (longAddress & 0xffffffffL));
		buffer.put(type);
		buffer.put(payload);
	}
	
	public static void appendReversedInt(ByteBuffer buffer, int value) {
		buffer.put(getByteValue(value, 24));
		buffer.put(getByteValue(value, 16));
		buffer.put(getByteValue(value, 8));
		buffer.put(getByteValue(value, 0));
	}
	
	public static void appendMPTNHeader(ByteBuffer buffer, int ipaddress, short port,
			int nodeId, byte type, byte payloadByte) {
		buffer.put((byte)0xAA);
		buffer.put((byte)0x55);
		buffer.put(getByteValue(nodeId, 0));
		appendReversedInt(buffer, ipaddress);
		buffer.put(new Integer(port%256).byteValue());
		buffer.put(new Integer(port/256).byteValue());
		buffer.put(type);
		buffer.put(payloadByte);
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
	
	public static ByteBuffer deepCopy(ByteBuffer original) {
		final ByteBuffer clone = (original.isDirect()) ?
				ByteBuffer.allocateDirect(original.capacity()) : ByteBuffer.allocate(original.capacity());
		clone.put(original);
		clone.order(original.order());
		clone.position(original.position());
		clone.flip();
		return clone;
	}
	
	private static byte getByteValue(int value, int deviation) {
		return new Integer(value >> deviation).byteValue();
	}
}
