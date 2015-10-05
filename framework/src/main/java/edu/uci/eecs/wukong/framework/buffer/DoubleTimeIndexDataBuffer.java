package edu.uci.eecs.wukong.framework.buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.nio.ByteBuffer;

import org.apache.commons.lang.ArrayUtils;

public final class DoubleTimeIndexDataBuffer<T> {
	private TimeIndexBuffer indexBuffer;
	private DataRingBuffer<T> dataBuffer;
	private BufferIndexer indexer;
	private int interval; //in seconds
	
	private class BufferIndexer extends TimerTask {
		private DoubleTimeIndexDataBuffer<T> buffer;
		public BufferIndexer(DoubleTimeIndexDataBuffer<T> buffer) {
			this.buffer = buffer;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			buffer.getTimeIndexBuffer().appendIndex(buffer.getDataRingBuffer().getHeader());
		}
		
	}
	
	public DoubleTimeIndexDataBuffer(int dataCapacity, int timeUnits, int interval){
		this.indexBuffer = new TimeIndexBuffer(timeUnits);
		this.dataBuffer = new DataRingBuffer<T>(dataCapacity);
		this.interval = interval;
		this.indexer = new BufferIndexer(this);
	}
	
	public void addElement(long timestampe,  T value) {
		this.dataBuffer.addElement(timestampe, value);
		System.out.println("Data Buffer Header: " + dataBuffer.getHeader());
	}
	
	public void addIndex() {
		indexBuffer.appendIndex(dataBuffer.getHeader());
		System.out.println("Index Buffer Header: " + indexBuffer.getHeader());
	}
	
	/**
	 * Read the data from units time ago util now.
	 * @param units before current time
	 * @return
	 */
	protected ByteBuffer read(int units) {
		int start = this.indexBuffer.getTimeIndex(units + 1);
		int end = this.indexBuffer.getTimeIndex(1);
		if (start < end) {
			int size = end - start;
			byte[] buf =  new byte[size];
			this.dataBuffer.get(buf, start, size);
			return ByteBuffer.wrap(buf);
		} else { //
			int firstSize = this.dataBuffer.getCapacity() - 1 - start;
			byte[] firstBuf = new byte[firstSize];
			byte[] secondBuf = new byte[start];
			this.dataBuffer.get(firstBuf, start, firstSize);
			this.dataBuffer.get(secondBuf, 0, start);
			
			return ByteBuffer.wrap(ArrayUtils.addAll(firstBuf, secondBuf));
		}
	}
	
	
	public List<DataPoint<Short>> readDataPoint(int units) {
		ByteBuffer data = read(units);
		int size = data.capacity() / DataRingBuffer.DATA_SIZE;
		List<DataPoint<Short>> points = new ArrayList<DataPoint<Short>>();
		while(size > 0) {
			points.add(new DataPoint<Short>(data.getInt(), data.getShort()));
			size --;
		}
		
		return points;
	}
	
	public int getInterval() {
		return this.interval;
	}
	
	public BufferIndexer getIndexer() {
		return this.indexer;
	}
	
	protected TimeIndexBuffer getTimeIndexBuffer() {
		return this.indexBuffer;
	}
	
	protected DataRingBuffer<T> getDataRingBuffer() {
		return this.dataBuffer;
	}
}
