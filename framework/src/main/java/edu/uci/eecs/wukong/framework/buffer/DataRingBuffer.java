package edu.uci.eecs.wukong.framework.buffer;

/**
 * A data point is composed by two parts. 4 bytes represents the time stamp deviation from
 * last data point, another 2 bytes represents the data value.
 * 
 * @author Peter
 *
 */
public final class DataRingBuffer extends RingBuffer {
	public static final int DATA_SIZE = 6;
	private int initial;
	private int last;
	
	public DataRingBuffer(int capacity) {
		super(DATA_SIZE * capacity);
		this.initial = 0;
		this.last = 0;
	}
	
	public synchronized void addElement(int time, short value) {
		if(initial == 0) {
			initial = time;
		}
		
		this.append(time - last);
		this.append(value);
		this.last = time;
	}
 }
