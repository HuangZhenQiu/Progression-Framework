package edu.uci.eecs.wukong.framework.model;

/**
 * Network Port Property (NPP) represents an unique stream in WuKong system.
 */
public class NPP {
	/* Network ID*/
	private int nid;
	/* Represent a particular WuObject in the server*/
	private byte portId;
	/* Represent a particular WuProperty of a WuClass*/
	private byte propertyId;
	
	public NPP(int nid, byte portId, byte propertyId) {
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
}
