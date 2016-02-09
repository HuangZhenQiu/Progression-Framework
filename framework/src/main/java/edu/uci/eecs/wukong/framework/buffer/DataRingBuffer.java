package edu.uci.eecs.wukong.framework.buffer;

/**
 * A data point is composed by two parts. 4 bytes represents the time stamp deviation from
 * last data point, another 2 bytes represents the data value.
 * 
 * @author Peter
 *
 */
public final class DataRingBuffer<T, E extends BufferUnit<T>> extends RingBuffer {
	private long initial;
	private long last;
	private int unitSize;
	private int capacity;
	private int size;
	private Class<E> type;
	
	public DataRingBuffer(int capacity, int unitSize, Class<E> type) {
		super(unitSize * capacity);
		this.capacity = capacity;
		this.unitSize = unitSize;
		this.type = type;
		this.initial = 0;
		this.last = 0;
	}
	
	public synchronized void addElement(long time, E value) {
		if(!(value instanceof BufferUnit)) {
			throw new IllegalArgumentException("Only support subclass of BufferUnit as type of data.");
		}
		
		if(initial == 0) {
			initial = time;
		}
		
		// Add time deviation
		this.appendInt((int)(time - last));
		this.append(value.toArray());
		this.last = time;
		this.size = (size ++) % capacity;
	}
	
	public int getSize() {
		return this.size;
	}
	
	public int getUnitSize() {
		return this.unitSize;
	}
	
	public int getCapacity() {
		return this.capacity;
	}
 }
