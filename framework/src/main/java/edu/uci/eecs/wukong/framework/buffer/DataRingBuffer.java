package edu.uci.eecs.wukong.framework.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A data point is composed by two parts. 4 bytes represents the time stamp deviation from
 * last data point, another 2 bytes represents the data value.
 * 
 * @author Peter
 *
 */
public final class DataRingBuffer<T, E extends BufferUnit<T>> extends RingBuffer {
	private static Logger logger = LoggerFactory.getLogger(DataRingBuffer.class);
	private long initial;
	private long last;
	private int unitSize;
	private int capacity;
	private int size;
	private Class<E> type;
	
	public DataRingBuffer(int capacity, int unitSize, Class<E> type) {
		super((unitSize + 4) * capacity);
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
	
	public synchronized List<T> getElements(int size) throws RuntimeException{	
		// in case size is bigger;
		if (size > capacity) {
			throw new RuntimeException("Fail to access size out of capacity");
		}
		
		int elementLength = 4 + unitSize;
		List<T> elements = new ArrayList<T> ();
		int totalBytes = size * (elementLength);
		byte[] bytes = new byte[totalBytes];
		this.getByteFromPosition(bytes, totalBytes);
		try {
			for (int i = 0; i < size; i++) {
				byte[] data = Arrays.copyOfRange(bytes, 4 + i * elementLength, (i + 1) * elementLength);
				BufferUnit<T> unit = type.getConstructor().newInstance();
				unit.parse(ByteBuffer.wrap(data));
				elements.add(unit.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Fail to create instance from BufferUnit Type: " + type);
		}
		
		return elements;
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
