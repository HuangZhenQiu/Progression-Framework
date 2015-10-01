package edu.uci.eecs.wukong.framework.model;

public class EndPoint {
	/* Network address */
	private int nodeId;
	private byte portId;
	
	public EndPoint(int nodeId, byte portId) {
		this.nodeId = nodeId;
		this.portId = portId;
	}
	
	public int getNodeId() {
		return nodeId;
	}
	
	public byte getPortId() {
		return portId;
	}
}
