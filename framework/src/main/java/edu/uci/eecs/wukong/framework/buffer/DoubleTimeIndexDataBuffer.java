package edu.uci.eecs.wukong.framework.buffer;

import org.apache.commons.lang.ArrayUtils;

public final class DoubleTimeIndexDataBuffer {
	private TimeIndexBuffer indexBuffer;
	private DataRingBuffer dataBuffer;
	private int interval; //in seconds
	
	public DoubleTimeIndexDataBuffer(int dataCapacity, int timeUnits, int interval){
		this.indexBuffer = new TimeIndexBuffer(timeUnits);
		this.dataBuffer = new DataRingBuffer(dataCapacity);
		this.interval = interval;
	}
	
	public void addElement(int timestampe,  short value) {
		this.dataBuffer.addElement(timestampe, value);
	}
	
	public void addIndex() {
		indexBuffer.put(dataBuffer.getHeader());
	}
	
	/**
	 * Read the data from units time ago to now.
	 * @param units before current time
	 * @return
	 */
	public byte[] read(int units) {
		int start = this.indexBuffer.getTimeIndex(units);
		int end = this.indexBuffer.getInteger(indexBuffer.getHeader());
		if (start < end) {
			int size = start - end;
			byte[]  buf=  new byte[size];
			this.dataBuffer.get(buf, start, size);
			return buf;
		} else { //
			int firstSize = this.dataBuffer.getCapacity() - 1 - start;
			byte[] firstBuf = new byte[firstSize];
			byte[] secondBuf = new byte[start];
			this.dataBuffer.get(firstBuf, start, firstSize);
			this.dataBuffer.get(secondBuf, 0, start);
			
			return ArrayUtils.addAll(firstBuf, secondBuf);
		}
	}
	
}
