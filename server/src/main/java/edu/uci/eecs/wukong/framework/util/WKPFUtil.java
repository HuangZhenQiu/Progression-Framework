package edu.uci.eecs.wukong.framework.util;

import java.nio.ByteBuffer;

public class WKPFUtil {
	public static final byte DEFAULT_OBJECT_SIZE = 4;
	public static final byte DEFAULT_CLASS_SIZE = 3;
	public static final byte DEFAULT_WKCOMM_MESSAGE_PAYLOAD_SIZE = 40;
	public static final byte DEFAULT_OBJECT_NUMBER = ((DEFAULT_WKCOMM_MESSAGE_PAYLOAD_SIZE-3)/DEFAULT_OBJECT_SIZE);
	
	/* Virtual and Can create Instance, It should be 3 in the end. Temporarily, we treat it as  */
	public static final byte PLUGIN_WUCLASS_TYPE = 0; // Need to confirm with Niels
	public static final int WKPF_WRITE_PROPERTY_LENGTH = 8; // Until type 
	
	public static final byte WKPF_PROPERTY_TYPE_SHORT = 0;
	public static final byte WKPF_PROPERTY_TYPE_BOOLEAN = 1;
	public static final byte WKPF_PROPERTY_TYPE_REFRESH_RATE = 2;
	public static final byte WKPF_PROPERTY_TYPE_LOCATION = 3;
	public static final byte WKPF_PROPERTY_TYPE_ACTIVITY = 4;
	public static final byte WKPF_PROPERTY_TYPE_RESPONSE = 5;
	
	public static final byte WKPF_REPRG_OPEN                 = 0x10;
	public static final byte WKPF_REPRG_OPEN_R               = 0x11;
	public static final byte WKPF_REPRG_WRITE                = 0x12;
	public static final byte WKPF_REPRG_WRITE_R              = 0x13;
	public static final byte WKPF_REPRG_COMMIT               = 0x14;
	public static final byte WKPF_REPRG_COMMIT_R             = 0x15;
	public static final byte WKPF_REPRG_REBOOT               = 0x16;
	
	public static final byte WKPF_REPROG_OK                  = 0x00;
	public static final byte WKPF_REPROG_REQUEST_RETRANSIMIT = 0x01;
	public static final byte WKPF_REPROG_TOOLARGE            = 0x02;
	public static final byte WKPF_REPROG_FAILED              = 0x03;
	
	public static final byte WKPF_GET_LINK_COUNTER        = (byte)0x60;
	public static final byte WKPF_GET_LINK_COUNTER_R      = (byte)0x61;
	
	public static final byte WKPF_GET_WUCLASS_LIST        = (byte)0x90;
	public static final byte WKPF_GET_WUCLASS_LIST_R      = (byte)0x91;
	public static final byte WKPF_GET_WUOBJECT_LIST       = (byte)0x92;
	public static final byte WKPF_GET_WUOBJECT_LIST_R     = (byte)0x93;
	public static final byte WKPF_READ_PROPERTY	          = (byte)0x94;
	public static final byte WKPF_READ_PROPERTY_R         = (byte)0x95;
	public static final byte WKPF_WRITE_PROPERTY	      = (byte)0x96;
	public static final byte WKPF_WRITE_PROPERTY_R	      = (byte)0x97;
	public static final byte WKPF_REQUEST_PROPERTY_INIT   = (byte)0x98;
	public static final byte WKPF_REQUEST_PROPERTY_INIT_R = (byte)0x99;
	public static final byte WKPF_GET_LOCATION            = (byte)0x9A;
	public static final byte WKPF_GET_LOCATION_R          = (byte)0x9B;
	public static final byte WKPF_SET_LOCATION            = (byte)0x9C;
	public static final byte WKPF_SET_LOCATION_R          = (byte)0x9D;
	
	public static final byte MONITORING                   = (byte)0xB5;
	public static final byte WKPF_ERROR                   = (byte)0x86;
	
	
	public static void appendWKPFPacket(ByteBuffer buffer, long sourceId, long destId, byte type, byte[] payload) {
		// MPTNUtil.appendReversedInt(buffer, destId);
		// MPTNUtil.appendReversedInt(buffer, (int) (sourceId & 0xffffffffL));
		buffer.put((byte)(destId >> 24));
		buffer.put((byte)(destId >> 16));
		buffer.put((byte)(destId >> 8));
		buffer.put((byte)(destId >> 0));
		buffer.put((byte)(sourceId >> 24));
		buffer.put((byte)(sourceId >> 16));
		buffer.put((byte)(sourceId >> 8));
		buffer.put((byte)(sourceId >> 0));
		buffer.put(type);
		if (payload != null) {
			buffer.put(payload);
		}
	}
	
	/**
	 * Get little endian short from the start index of the buffer
	 * @param start the index in the buffer
	 * @return the converted short
	 */
	public static short getBigEndianShort(byte[] buffer, int start) {
		return (short) (getUnsignedByteValue(buffer[start + 1]) * 256 + getUnsignedByteValue(buffer[start]));
	}
	
	public static byte[] getBigEndianIntegerBytes(byte[] buffer, int start) {
		byte[] b = new byte[4];
		b[0] = buffer[start + 3];
		b[1] = buffer[start + 2];
		b[2] = buffer[start + 1];
		b[3] = buffer[start];
		
		return b;
	}
	
	public static long getLong(byte[] buffer, int start) {
		long result = ((new Long(buffer[start]) + 256) % 256) << 24;
		result += ((new Long(buffer[start + 1]) + 256) % 256) << 16;
		result += ((new Long(buffer[start + 2]) + 256) % 256) << 8;
		result += ((new Long(buffer[start + 3]) + 256) % 256);
		
		return result;
	}
	
	/**
	 * Get big endian int from the start index of the buffer
	 * @param start the index of the buffer
	 * @return the converted int
	 */
	public static int getBigEndianInteger(byte[] buffer, int start) {
		int result = buffer[start];
		result += buffer[start + 1] << 8;
		result += buffer[start + 2] << 16;
		result += buffer[start + 3] << 24;
		
		return (int) (result & 0xffffffffL);
	}
	
	public static long getBigEndianLong(byte[] buffer, int start) {
		long result = (new Long(buffer[start]) + 256) % 256;
		result += ((new Long(buffer[start + 1]) + 256) % 256) << 8;
		result += ((new Long(buffer[start + 2]) + 256) % 256) << 16;
		result += ((new Long(buffer[start + 3]) + 256) % 256) << 24;
		
		return result;
	}
	/**
	 * Convert java signed byte to unsigned value
	 * @param data signed java byte
	 * @return unsigned byte
	 */
	public static int getUnsignedByteValue(byte data) {
		if (data < 0) {
			return 256 + data;
		}
		
		return data;
	}
}
