package edu.uci.eecs.wukong.framework.util;

import java.nio.ByteBuffer;
import java.util.UUID;

import com.google.common.primitives.UnsignedInteger;

import edu.uci.eecs.wukong.framework.mptn.packet.TCPMPTNPacket;
public class MPTNUtil {
	
	public final static int MPTN_ID_LEN = 4;
	public final static int MPTN_MASTER_ID = 0;
	public final static int MPTN_PROGRESSION_SERVER = 1;
	public final static UnsignedInteger MPTN_MAX_ID = UnsignedInteger.MAX_VALUE;
	public final static int MPTN_MSGTYPE_LEN  = 1;
	public final static int MPTN_DEST_BYTE_OFFSET = 0;
	public final static int MPTN_SRC_BYTE_OFFSET = MPTN_DEST_BYTE_OFFSET + MPTN_ID_LEN;
	public final static int MPTN_MSATYPE_BYTE_OFFSET = MPTN_SRC_BYTE_OFFSET + MPTN_ID_LEN;
	public final static int MPTN_PAYLOAD_BYTE_OFFSET = MPTN_MSATYPE_BYTE_OFFSET + MPTN_MSGTYPE_LEN;
	
	public final static int MPTN_TCP_NOUNCE_SIZE = 8;
	public final static int MPTN_TCP_PACKAGE_SIZE = 4;
	public final static int MPTN_TCP_HEADER_LENTGH = MPTN_ID_LEN + MPTN_TCP_NOUNCE_SIZE + MPTN_TCP_PACKAGE_SIZE;
	
	public static final int MPTN_HEADER_LENGTH = 9;
	public static final byte HEADER_TYPE_1 = 1;
	public static final byte HEADER_TYPE_2 = 2;
	
	public static final int CONNECTION_RETRIES = 1;
	public static final int NETWORK_TIMEOUT = 3;
	
	// transport interface sub network length
	public static final int ZW_ADDRESS_LEN = 1;
	public static final int ZB_ADDRESS_LEN = 2;
	public static final int IP_ADDRESS_LEN = 4;
	
	// MODE for adding and deleting node
	public static final int STOP_MODE = 0;
	public static final int ADD_MODE = 1;
	public static final int DEL_MODE = 2;
	
	// protocol handler admission control
	public static final int ONLY_FROM_TCP_SERVER = 1; // Master or peer gateway
	public static final int ONLY_FROM_TRANSPORT_INTERFACE = 2;
	public static final int VALID_FROM_ALL = 3;
	
	// ID service
	public static final byte MPTN_MSGTYPE_GWDISCOVER = 0;
	public static final byte MPTN_MSGTYPE_GWOFFER = 1;
	public static final byte MPTN_MSGTYPE_IDREQ = 2;
	public static final byte MPTN_MSGTYPE_IDACK = 3;
	public static final byte MPTN_MSGTYPE_IDNAK = 4;
	public static final byte MPTN_MSGTYPE_GWIDREQ = 5;
	public static final byte MPTN_MSGTYPE_GWIDACK = 6;
	public static final byte MPTN_MSGTYPE_GWIDNAK = 7;
	
    // Heartbeat
	public static final byte MPTN_MSGTYPE_RTPING = 8;
	public static final byte MPTN_MSGTYPE_RTREQ = 9;
	public static final byte MPTN_MSGTYPE_RTREP = 10;
	
	// RPC service with master
	public static final byte MPTN_MSGTYPE_RPCCMD = 16;
	public static final byte MPTN_MSGTYPE_RPCREP = 17;
	
	// Message forward
	public static final byte MPTN_MSGTYPE_FWDREQ = 24;
	public static final byte MPTN_MSGTYPE_FWDACK = 25;
	public static final byte MPTN_MSGTYPE_FWDNAK = 26;
	
	public static final byte MPTN_ERROR = 99;
	
	public static final int MASTER_ID = 0;
	public static final int MPTN_UDP_PORT = 5775;
	
	public static final int GWIDREQ_PAYLOAD_LEN = 16;
	public static final int IDREQ_PAYLOAD_LEN = 16;
	
	public static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	public static String toHexString(byte[] bytes) {
		if (bytes != null) {
		    char[] hexChars = new char[bytes.length * 2];
		    int v;
		    for (int j = 0; j < bytes.length; j++) {
		      v = bytes[j] & 0xFF;
		      hexChars[j * 2] = HEX_CHARS[v >>> 4];
		      hexChars[j * 2 + 1] = HEX_CHARS[v & 0x0F];
		    }
		    return new String(hexChars);
		} else {
			return "";
		}
	}
	
	public static long getNetMaskValue(int length) {
		long mask = (long)Math.pow(2, length) - 1;
		return mask << (32 - length);
	}
	
	public static byte[] getUUIDBytes() {
		UUID uuid = UUID.randomUUID();
		ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
		appendUnsignedBytes(bb, uuid.getMostSignificantBits());
		appendUnsignedBytes(bb, uuid.getLeastSignificantBits());
		return bb.array();
	}
	
	public static void appendUnsignedBytes(ByteBuffer buffer, long value) {
		for (int i = 7; i >= 0; i--) {
			byte v = getByteValue(value, 8 * i);
			buffer.put((byte)((v + 256) % 128));
		}
	}
	
	public static ByteBuffer createBufferFromTCPMPTNPacket(TCPMPTNPacket packet) {
		ByteBuffer buffer;
		if (packet.getPeerId() == MASTER_ID) {
			buffer = ByteBuffer.allocate(MPTNUtil.MPTN_TCP_HEADER_LENTGH + (int)packet.getLength());
			buffer.putInt(packet.getPeerId());
			buffer.putLong(packet.getNounce());
			appendReversedInt(buffer, packet.getLength());
			buffer.put(packet.getPayload());
		} else { // It is reply message
			buffer = ByteBuffer.allocate(12 + (int)packet.getLength());
			buffer.putLong(packet.getNounce());
			appendReversedInt(buffer, packet.getLength());
			buffer.put(packet.getPayload());
		}
		return buffer;
	}
	
	public static void appendReversedInt(ByteBuffer buffer, int value) {
		buffer.put(getByteValue(value, 0));
		buffer.put(getByteValue(value, 8));
		buffer.put(getByteValue(value, 16));
		buffer.put(getByteValue(value, 24));
	}
	
	public static void appendUDPMPTNHeader(ByteBuffer buffer, int ipaddress, short port,
			int nodeId, byte type, byte length) {
		buffer.put((byte)0xAA);
		buffer.put((byte)0x55);
		buffer.put(getByteValue(nodeId, 0));
		appendReversedInt(buffer, ipaddress);
		buffer.put(new Integer(port%256).byteValue());
		buffer.put(new Integer(port/256).byteValue());
		buffer.put(type);
		buffer.put(length);
	}
	
	public static void appendMPTNPackage(ByteBuffer buffer, int source, int dest, byte type, byte[] payload) {
		buffer.putInt(dest);
		buffer.putInt(source);
		buffer.put(type);
		buffer.put(payload);
	}
	
	public static int IPToInteger(String ipstr) {
		int result = 0;
		String[] addresses =  ipstr.split("\\.");
		for (int i=3; i >=0; i--) {
			int ip = Integer.parseInt(addresses[3 - i]);
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
	
	private static byte getByteValue(long value, int deviation) {
		return new Long(value >> deviation).byteValue();
	}
}
