package edu.uci.eecs.wukong.framework.buffer;

/**
 * A data point is composed by two parts. 4 bytes represents the time stamp deviation from
 * last data point, another 2 bytes represents the data value.
 * 
 * @author Peter
 *
 */
public final class DataRingBuffer<T, E extends BufferUnit<T>> extends RingBuffer {
	public static final int DATA_SIZE = 6;
	private long initial;
	private long last;
	private Class<E> type;
	
	public DataRingBuffer(int capacity, Class<E> type) {
		super(DATA_SIZE * capacity);
		this.type = type;
		this.initial = 0;
		this.last = 0;
	}
	
	public synchronized void addElement(long time, T value) {
		if(!(value instanceof BufferUnit)) {
			throw new IllegalArgumentException("Only support subclass of BufferUnit as type of data.");
		}
		
		if(initial == 0) {
			initial = time;
		}
		
		// Add time deviation
		this.appendInt((int)(time - last));
		
		this.last = time;
	}
	
	public int getSize() {
		return this.buffer.capacity();
	}
 }
