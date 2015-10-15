package edu.uci.eecs.wukong.framework.model;

public class EndPoint {
	/* Network address */
	private long nodeId;
	private byte portId;
	
	public EndPoint(long nodeId, byte portId) {
		this.nodeId = nodeId;
		this.portId = portId;
	}
	
	public long getNodeId() {
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
		return (int)nodeId * base + portId;
	}
	
	@Override
	public String toString() {
		return "EndPoint[NodeId = " + nodeId + ", PortId = " + portId + "]";
	}
}
