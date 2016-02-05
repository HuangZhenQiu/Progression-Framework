package edu.uci.eecs.wukong.framework.model;

/**
 * Sensor data model is the raw data that will be kept in mixed data buffer. Each data point
 * contains whether it from, so that we may use them for feature extraction in operator.
 * 
 * 
 * @author peter
 */
public class SensorData {
	private int nodeId;
	private byte propertyId;
	private byte portId;
	private short value;
	
	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}
	public byte getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(byte propertyId) {
		this.propertyId = propertyId;
	}
	public byte getPortId() {
		return portId;
	}
	public void setPortId(byte portId) {
		this.portId = portId;
	}
	public short getValue() {
		return value;
	}
	public void setValue(short value) {
		this.value = value;
	}
}
