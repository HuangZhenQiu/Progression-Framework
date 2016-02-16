package edu.uci.eecs.wukong.framework.buffer;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.nio.ByteBuffer;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.NPP;

public final class DoubleTimeIndexDataBuffer<T, E extends BufferUnit<T>> {
	private static Logger logger = LoggerFactory.getLogger(DoubleTimeIndexDataBuffer.class);
	private TimeIndexBuffer indexBuffer;
	private DataRingBuffer<T, E> dataBuffer;
	private BufferIndexer indexer;
	private Class<E> type;
	private NPP npp;
	private int interval; //in seconds
	
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
	
	public DoubleTimeIndexDataBuffer(NPP npp, Class<E> type, int unitSize,
			int dataCapacity, int timeUnits, int interval){
		this.npp = npp;
		this.type = type;
		this.indexBuffer = new TimeIndexBuffer(timeUnits);
		this.dataBuffer = new DataRingBuffer<T, E>(dataCapacity, unitSize, type);
		this.interval = interval;
		this.indexer = new BufferIndexer(this);
	}
	
	public void addElement(long timestampe,  E value) {
		this.dataBuffer.addElement(timestampe, value);
		logger.info("Data Buffer for " + npp + " Header: " + dataBuffer.getHeader());
	}
	
	public void addIndex() {
		indexBuffer.appendIndex(dataBuffer.getHeader());
		logger.info("Index Buffer " + npp + " Header: " + indexBuffer.getHeader());
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
	
	
	public List<DataPoint<T>> readDataPoint(int units) {
		ByteBuffer data = read(units);
		int size = data.capacity() / dataBuffer.getUnitLength();
		List<DataPoint<T>> points = new ArrayList<DataPoint<T>>();
		while(size > 0) {
			try {
				BufferUnit<T> unit = (BufferUnit<T>)type.getConstructor().newInstance();
				unit.parse(data, false);
				points.add(new DataPoint<T>(npp, data.getInt(), unit.getValue()));
			} catch (Exception e) {
				
			}
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
	
	protected DataRingBuffer<T, E> getDataRingBuffer() {
		return this.dataBuffer;
	}
	
	public int getSize() {
		return indexBuffer.getSize() + dataBuffer.getSize();
	}
}
