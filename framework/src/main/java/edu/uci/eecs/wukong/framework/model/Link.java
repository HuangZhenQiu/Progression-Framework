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
}
