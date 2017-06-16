package edu.uci.eecs.wukong.framework.model;

import java.util.HashMap;
import java.util.Map;

public enum WKPFMessageType {
	WriteProperty("WriteProperty"),
	GetCounterReturn("GetCounterReturn"),
	GetDeviceStatusReturn("GetDeviceStatusReturn"),
	SetLockReturn("SetLockReturn"),
	ChangeLinkReturn("ChangeLinkReturn"),
	ChangeComponentMapReturn("ChangeComponentMapReturn"),
	ReleaseLockReturn("ReleaseLockReturn");
	
	private final String name;
	// Map WKPF message type to the enum type
	private static Map<Byte, WKPFMessageType> messageMap = new HashMap<Byte, WKPFMessageType>();
	
	static {
		messageMap.put((byte)0x95, WriteProperty);
		messageMap.put((byte)0xB2, GetCounterReturn);
		messageMap.put((byte)0xB4, GetDeviceStatusReturn);
		messageMap.put((byte)0xB8, ChangeComponentMapReturn);
		messageMap.put((byte)0xA7, SetLockReturn);
		messageMap.put((byte)0xA5, ChangeLinkReturn);
		messageMap.put((byte)0xA9, ReleaseLockReturn);
	}
	
	public WKPFMessageType getMessageType(byte messageCode) {
		return messageMap.get(messageCode);
	}
	
	private WKPFMessageType(String name) {
		this.name = name;
	}
}
