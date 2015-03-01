package edu.uci.eecs.wukong.framework.buffer;


/**
 * TimeIndexBuffer is to record the first index of a region of data with in an interval.
 * The unit of a TimeIndex could be any seconds;
 * 
 * @author Peter Huang
 *
 */
public final class TimeIndexBuffer extends RingBuffer {
	public TimeIndexBuffer(int capacity) {
		// an integer to record the index on data buffer
		super(4 * capacity);
	}
	
	public int getTimeIndex(int index) {
		return this.getReverseInteger(index);
	}
	
	public int getCurrentTimeIndex() {
		return this.getInteger(this.getHeader());
	}
	
	/**
	 * Put the index of data buffer at current time interval
	 * @param pos
	 */
	public void putIndex(int pos) {
		this.put(pos);
	}
}
