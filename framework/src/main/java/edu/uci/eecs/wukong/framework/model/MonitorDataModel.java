package edu.uci.eecs.wukong.framework.model;

public class MonitorDataModel {
	private int nodeId;
	private int wuClassId;
	private short port;
	private short propertyNumber;
	private short value;
	private double timestamp;
	
	public MonitorDataModel(int nodeId, int wuClassId, short port, short propertyNumber,
			short value, double timestamp) {
		this.nodeId = nodeId;
		this.wuClassId = wuClassId;
		this.port = port;
		this.propertyNumber = propertyNumber;
		this.value = value;
		this.timestamp = timestamp;
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getWuClassId() {
		return wuClassId;
	}

	public void setWuClassId(int wuClassId) {
		this.wuClassId = wuClassId;
	}

	public short getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}

	public short getPropertyNumber() {
		return propertyNumber;
	}

	public void setPropertyNumber(short propertyNumber) {
		this.propertyNumber = propertyNumber;
	}

	public short getValue() {
		return value;
	}

	public void setValue(short value) {
		this.value = value;
	}

	public double getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(double timestamp) {
		this.timestamp = timestamp;
	}
}
