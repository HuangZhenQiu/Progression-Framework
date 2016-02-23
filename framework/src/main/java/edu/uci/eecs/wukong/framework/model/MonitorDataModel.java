package edu.uci.eecs.wukong.framework.model;

public class MonitorDataModel {
	private long nodeId;
	private short wuClassId;
	private byte port;
	private byte propertyNumber;
	private byte type;
	private int length;
	private byte[] value;
	private double timestamp;
	
	public MonitorDataModel(long nodeId, short wuClassId, byte port, byte propertyNumber, byte type,
			int length, byte[] value, double timestamp) {
		this.nodeId = nodeId;
		this.wuClassId = wuClassId;
		this.port = port;
		this.propertyNumber = propertyNumber;
		this.type = type;
		this.length = length;
		this.value = value;
		this.timestamp = timestamp;
	}

	public long getNodeId() {
		return nodeId;
	}

	public void setNodeId(long nodeId) {
		this.nodeId = nodeId;
	}

	public short getWuClassId() {
		return wuClassId;
	}

	public void setWuClassId(short wuClassId) {
		this.wuClassId = wuClassId;
	}

	public byte getPort() {
		return port;
	}

	public void setPort(byte port) {
		this.port = port;
	}

	public byte getPropertyNumber() {
		return propertyNumber;
	}

	public void setPropertyNumber(byte propertyNumber) {
		this.propertyNumber = propertyNumber;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public void setLength(int length) {
		this.length = length;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}
}
