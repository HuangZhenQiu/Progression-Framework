package edu.uci.eecs.wukong.framework.buffer;

import java.lang.IllegalArgumentException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * It is a ring buffer implementation on a direct buffer of Java NIO. 
 * 
 * @author Peter
 *
 */
public class RingBuffer {
	private ByteBuffer buffer;
	private int header;
	private int size;
	private int capacity;
	
	public RingBuffer(int capacity) {
		this.buffer = ByteBuffer.allocateDirect(capacity);
		this.buffer.order(ByteOrder.BIG_ENDIAN);
		this.header = 0;
		this.size = 0;
		this.capacity = capacity;
	}
	
	public Short getShort(int index) {
		if(index > size)
			throw new IllegalArgumentException();
		return buffer.getShort(index);
	}
	
	public int getInteger(int index) {
		return buffer.getInt(index);
	}
	
	public int getReverseInteger(int index) {
		return buffer.getInt((header - index + capacity) % capacity);
	}
	
	/**
	 * 
	 * @param dst the array data should be written to
	 * @param offset offset in the ring buffer
	 * @param length
	 */
	public void get(byte[] dst, int offset, int length) {
		for(int i=0; i < length; i++) {
			dst[i] = buffer.get(offset + i);
		}
	}
	
	protected void put(int content) {
		buffer.putInt(header, content);
	}
	
	public synchronized void append(int content) {
		buffer.putInt(header, content);
		header = (header + 4) % capacity;
		if (size + 4 <= capacity) {
			size += 4;
		}
	}
	
	public synchronized void append(short content) {
		buffer.putShort(header, content);
		header = (header + 2) % capacity;
		if (size + 2 <= getCapacity()) {
			size += 2;
		}
	}
	
	public int getHeader() {
		return this.header;
	}
	
	public int getCapacity() {
		return buffer.capacity();
	}
}
