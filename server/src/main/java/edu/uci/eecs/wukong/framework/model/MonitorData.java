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
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof MonitorData) {
			MonitorData model = (MonitorData) object;
			if (model.nodeId == this.nodeId
					&& model.portId == this.portId
					&& model.value == this.value) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = nodeId;
		code = code * base + portId;
		code = (int) (code * base + value);
		return code;
	}
}
