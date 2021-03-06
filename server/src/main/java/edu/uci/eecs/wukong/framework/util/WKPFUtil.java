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
	
	public static final byte WKPF_OK = 0;
	public static final byte WKPF_ERR_WUOBJECT_NOT_FOUND = 1;
	public static final byte WKPF_ERR_PROPERTY_NOT_FOUND = 2;
	public static final byte WKPF_ERR_WUCLASS_NOT_FOUND = 3;
	public static final byte WKPF_ERR_READ_ONLY = 4;
	public static final byte WKPF_ERR_WRITE_ONLY = 5;
	public static final byte WKPF_ERR_PORT_IN_USE = 6;
	public static final byte WKPF_ERR_WUCLASS_ID_IN_USE = 7;
	public static final byte WKPF_ERR_OUT_OF_MEMORY = 8;
	public static final byte WKPF_ERR_WRONG_DATATYPE = 9;
	public static final byte WKPF_ERR_WUOBJECT_ALREADY_ALLOCATED = 10;
	public static final byte WKPF_ERR_NEED_VIRTUAL_WUCLASS_INSTANCE = 11;
	public static final byte WKPF_ERR_NVMCOMM_SEND_ERROR = 12;
	public static final byte WKPF_ERR_NVMCOMM_NO_REPLY = 13;
	public static final byte WKPF_ERR_REMOTE_PROPERTY_FROM_JAVASET_NOT_SUPPORTED = 14;
	public static final byte WKPF_ERR_COMPONENT_NOT_FOUND = 15;
	public static final byte WKPF_ERR_LOCATION_TOO_LONG = 16;
	public static final byte WKPF_ERR_UNKNOWN_FEATURE = 17;
	public static final byte WKPF_ERR_LINK_NOT_FOUND = 18;
	public static final byte WKPF_ERR_ENDPOINT_NOT_FOUND = 19;
	public static final byte WKPF_ERR_CANT_CREATE_INSTANCE_OF_WUCLASS = 20;
	public static final byte WKPF_ERR_LOCK_FAIL = 21;
	public static final byte WKPF_ERR_UNLOCK_FAIL = 22;
	public static final byte WKPF_LOCKED = 23;
	public static final byte WKPF_ERR_SHOULDNT_HAPPEN = (byte)0xFF;
	
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
	
	public static final byte WKPF_GET_LINK_COUNTER        = (byte)0xB1;
	public static final byte WKPF_GET_LINK_COUNTER_R      = (byte)0xB2;
	public static final byte WKPF_GET_DEVICE_STATUS       = (byte)0xB3;
	public static final byte WKPF_GET_DEVICE_STATUS_R     = (byte)0xB4;
	
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
	public static final byte WKPF_GET_FEATURE             = (byte)0x9E;
	public static final byte WKPF_GET_FEATURE_R           = (byte)0x9F;
	public static final byte WKPF_SET_FEATURE             = (byte)0xA0;
	public static final byte WKPF_SET_FEATURE_R           = (byte)0xA1;
	public static final byte WKPF_CHANGE_MAP              = (byte)0xA2;
	public static final byte WKPF_CHANGE_MAP_R            = (byte)0xA3;
	
	
	public static final byte WKPF_CHANGE_LINK             = (byte)0xA4;
	public static final byte WKPF_CHANGE_LINK_R           = (byte)0xA5;
	public static final byte WKPF_SET_LOCK                = (byte)0xA6;
	public static final byte WKPF_SET_LOCK_R              = (byte)0xA7;
	public static final byte WKPF_RELEASE_LOCK            = (byte)0xA8;
	public static final byte WKPF_RELEASE_LOCK_R          = (byte)0xA9;
	public static final byte WKPF_ERROR_R                 = (byte)0xAF;
	
	
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
		int result = (int)getUnsignedByteValue(buffer[start]);
		result += getUnsignedByteValue(buffer[start + 1]) << 8;
		result += getUnsignedByteValue(buffer[start + 2]) << 16;
		result += getUnsignedByteValue(buffer[start + 3]) << 24;
		
		return (int) (result & 0xffffffffL);
	}
	
	public static long getBigEndianLong(byte[] buffer, int start) {
		long result = getUnsignedByteValue(buffer[start]);
		result += getUnsignedByteValue(buffer[start + 1]) << 8;
		result += getUnsignedByteValue(buffer[start + 2]) << 16;
		result += getUnsignedByteValue(buffer[start + 3]) << 24;
		
		return result;
	}
	/**
	 * Convert java signed byte to unsigned value
	 * @param data signed java byte
	 * @return unsigned byte
	 */
	public static long getUnsignedByteValue(byte data) {
		if (data < 0) {
			return (long)(256 + data);
		}
		
		return data;
	}
}
