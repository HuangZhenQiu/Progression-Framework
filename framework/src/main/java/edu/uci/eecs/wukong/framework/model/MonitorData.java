package edu.uci.eecs.wukong.framework.model;

public class MonitorData {
	private int nodeId;
	private short portId;
	private double value;
	
	public MonitorData (int nodeId, short portId, double value) {
		this.nodeId = nodeId;
		this.portId = portId;
		this.value = value;
	}
	
	public Double getValue() {
		return value;
	}
}
