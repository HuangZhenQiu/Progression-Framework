package edu.uci.eecs.wukong.framework.model;

/**
 * Network Port Property (NPP) represents an unique stream in WuKong system.
 */
public class NPP {
	/* Network ID*/
	private long nid;
	/* Represent a particular WuObject in the server*/
	private byte portId;
	/* Represent a particular WuProperty of a WuClass*/
	private byte propertyId;
	
	public NPP(long nid, byte portId, byte propertyId) {
		this.nid = nid;
		this.portId = portId;
		this.propertyId = propertyId;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof NPP) {
			NPP model = (NPP) object;
			if (model.nid == this.nid
					&& model.portId == this.portId
					&& model.propertyId == this.propertyId) {
				return true;
			}
			
		}
			
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = (int) (nid * base + portId);
		return code * base + propertyId;
	}

	public long getNid() {
		return nid;
	}

	public void setNid(long nid) {
		this.nid = nid;
	}

	public byte getPortId() {
		return portId;
	}

	public void setPortId(byte portId) {
		this.portId = portId;
	}

	public byte getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(byte propertyId) {
		this.propertyId = propertyId;
	}
	
	@Override
	public String toString() {
		return "NPP[nid=" + nid + ", portId=" + portId + ", propertyId=" + propertyId + "]";
	}
	
}
