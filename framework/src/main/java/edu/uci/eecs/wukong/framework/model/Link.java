package edu.uci.eecs.wukong.framework.model;

public class Link {
	/* Component Id */
	private int sourceId;
	/* Property Id */
	private byte sourcePid;
	/* Component Id */
	private int destId;
	/* Property Id */
	private byte destPid;
	
	public Link(int sourceId, byte sourcePid, int destId, byte destPid) {
		this.sourceId = sourceId;
		this.sourcePid = sourcePid;
		this.destId = destId;
		this.destPid = destPid;
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public byte getSourcePid() {
		return sourcePid;
	}

	public void setSourcePid(byte sourcePid) {
		this.sourcePid = sourcePid;
	}

	public int getDestId() {
		return destId;
	}

	public void setDestId(int destId) {
		this.destId = destId;
	}

	public byte getDestPid() {
		return destPid;
	}

	public void setDestPid(byte destPid) {
		this.destPid = destPid;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Link) {
			Link model = (Link) object;
			if (model.destId == this.destId
					&& model.destPid == this.destPid
					&& model.sourceId == this.sourceId
					&& model.sourcePid == this.sourcePid) {
				return true;
			}
				
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = sourceId;
		code = code * base + sourcePid;
		code = code * base + destId;
		code = code * base + destPid;
		return code;
	}
}
