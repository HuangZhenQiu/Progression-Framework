package edu.uci.eecs.wukong.framework.buffer;

/**
 * A data point is composed by two parts. 4 bytes represents the time stamp deviation from
 * last data point, another 2 bytes represents the data value.
 * 
 * @author Peter
 *
 */
public final class DataRingBuffer<T extends Object> extends RingBuffer {
	public static final int DATA_SIZE = 6;
	private long initial;
	private long last;
	
	public DataRingBuffer(int capacity) {
		super(DATA_SIZE * capacity);
		this.initial = 0;
		this.last = 0;
	}
	
	public synchronized void addElement(long time, T value) {
		if(!( value instanceof Byte || value instanceof Short || value instanceof Integer
				|| value instanceof Double || value instanceof Long)) {
			throw new IllegalArgumentException("Only support Byte, Integer, Double and Long as type of data.");
		}
		if(initial == 0) {
			initial = time;
		}
		
		// Add time deviation
		this.appendInt((int)(time - last));
		
		if (value instanceof Byte) {
			this.appendByte((Byte) value);
		} else if(value instanceof Short) {
			this.appendShort((Short) value);
		} else if (value instanceof Integer) {
			this.appendInt((Integer) value);
		} else if (value instanceof Double) {
			this.appendDouble((Double) value);
		} else {
			this.appendLong((Long) value);
		}
		
		this.last = time;
	}
	
	public int getSize() {
		return this.buffer.capacity();
	}
 }
