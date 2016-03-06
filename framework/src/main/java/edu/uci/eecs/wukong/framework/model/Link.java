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
	/* message counter */
	private short counter;
	
	public Link(short sourceId, byte sourcePid, short destId, byte destPid, short counter) {
		this.sourceId = sourceId;
		this.sourcePid = sourcePid;
		this.destId = destId;
		this.destPid = destPid;
		this.counter = counter;
	}
	
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.put((byte) (sourceId % 256));
		buffer.put((byte) (sourceId / 256));
		buffer.put(sourcePid);
		buffer.put((byte) (destId % 256));
		buffer.put((byte) (destId / 256));
		buffer.put(destPid);
		// Counter value don't need to put in.
		buffer.put((byte) 0);
		buffer.put((byte) 0);
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
	
	public short getCounter() {
		return counter;
	}
	
	public void setCounter(short counter) {
		this.counter = counter;
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
