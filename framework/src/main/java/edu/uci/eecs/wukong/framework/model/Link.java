package edu.uci.eecs.wukong.framework.model;

import java.nio.ByteBuffer;

public class Link {
	/* Component Id */
	private short sourceId;
	/* Property Id */
	private byte sourcePid;
	/* Component Id */
	private short destId;
	/* Property Id */
	private byte destPid;
	
	public Link(short sourceId, byte sourcePid, short destId, byte destPid) {
		this.sourceId = sourceId;
		this.sourcePid = sourcePid;
		this.destId = destId;
		this.destPid = destPid;
	}
	
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.putShort(sourceId);
		buffer.put(sourcePid);
		buffer.putShort(destId);
		buffer.put(destPid);
		return buffer.array();
	}

	public int getSourceId() {
		return sourceId;
	}

	public void setSourceId(short sourceId) {
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

	public void setDestId(short destId) {
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
	
	@Override
	public String toString() {
		return "Link[SourceId = " + sourceId
				+ ", SourcePid = " + sourcePid
				+ ", DestId = " + destId + ", DestPid = " + destPid + "]";
	}
}
