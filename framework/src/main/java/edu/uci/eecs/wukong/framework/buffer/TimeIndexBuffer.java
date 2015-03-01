package edu.uci.eecs.wukong.framework.buffer;


/**
 * TimeIndexBuffer is to record the first index of a region of data with in an interval.
 * The unit of a TimeIndex could be any seconds;
 * 
 * @author Peter Huang
 *
 */
public final class TimeIndexBuffer extends RingBuffer {
	private static final int INTEGER_LENGTH = 4;
	public TimeIndexBuffer(int capacity) {
		// an integer to record the index on data buffer
		super(4 * capacity);
		this.put(0); // Point the index of start point to the start of data buffer
	}
	
	/**
	 * Find the position of units ahead of current time.
	 * @param index
	 * @return
	 */
	public int getTimeIndex(int units) {
		return this.getReverseInteger(INTEGER_LENGTH * units);
	}
	
	public int getCurrentTimeIndex() {
		return this.getInteger(this.getHeader());
	}
	
	/**
	 * Put the index of data buffer at current time interval
	 * @param pos
	 */
	public void appendIndex(int pos) {
		this.append(pos);
	}
}
