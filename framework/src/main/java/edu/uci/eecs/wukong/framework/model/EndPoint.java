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
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof EndPoint) {
			EndPoint model = (EndPoint) object;
			if (this.nodeId == model.nodeId
					&& this.portId == model.portId) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		return nodeId * base + portId;
	}
}
