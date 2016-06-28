package edu.uci.eecs.wukong.framework.buffer;

import java.util.List;
import java.util.TimerTask;
import java.nio.ByteBuffer;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DoubleTimeIndexDataBuffer<T, E extends BufferUnit<T>> {
	private static Logger logger = LoggerFactory.getLogger(DoubleTimeIndexDataBuffer.class);
	protected TimeIndexBuffer indexBuffer;
	protected DataRingBuffer<T, E> dataBuffer;
	protected BufferIndexer indexer;
	protected Class<E> type;

	
	private class BufferIndexer extends TimerTask {
		private DoubleTimeIndexDataBuffer<T, E> buffer;
		public BufferIndexer(DoubleTimeIndexDataBuffer<T, E> buffer) {
			this.buffer = buffer;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			buffer.getTimeIndexBuffer().appendIndex(buffer.getDataRingBuffer().getHeader());
		}
		
	}
	
	public DoubleTimeIndexDataBuffer(Class<E> type, int unitSize,
			int dataCapacity, int timeUnits, int interval){
		this.type = type;
		this.indexBuffer = new TimeIndexBuffer(timeUnits);
		this.dataBuffer = new DataRingBuffer<T, E>(dataCapacity, unitSize, type);
		this.indexer = new BufferIndexer(this);
	}
	
	public abstract String getKey();
	
	public void addElement(long timestampe,  E value) {
		this.dataBuffer.addElement(timestampe, value);
		logger.debug("Data Buffer for " + getKey() + " Header: " + dataBuffer.getHeader() + " value added " + value);
	}
	
	public void addIndex() {
		indexBuffer.appendIndex(dataBuffer.getHeader());
		logger.debug("Index Buffer " + getKey() + " Header: " + indexBuffer.getHeader());
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
		} else if (start < this.dataBuffer.getCapacity()) { //
			int firstSize = this.dataBuffer.getCapacity() - 1 - start;
			byte[] firstBuf = new byte[firstSize];
			byte[] secondBuf = new byte[start];
			this.dataBuffer.get(firstBuf, start, firstSize);
			this.dataBuffer.get(secondBuf, 0, start);
			
			return ByteBuffer.wrap(ArrayUtils.addAll(firstBuf, secondBuf));
		}
		
		// No Date inside
		return ByteBuffer.allocate(0);
	}
	
	public abstract List<DataPoint<T>> readDataPoint(int units);

	
	public BufferIndexer getIndexer() {
		return this.indexer;
	}
	
	protected TimeIndexBuffer getTimeIndexBuffer() {
		return this.indexBuffer;
	}
	
	protected DataRingBuffer<T, E> getDataRingBuffer() {
		return this.dataBuffer;
	}
	
	public int getSize() {
		return indexBuffer.getSize() + dataBuffer.getSize();
	}
}
