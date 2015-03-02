package edu.uci.eecs.wukong.framework.manager;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.lang.IllegalArgumentException;

import edu.uci.eecs.wukong.framework.ProgressionKey.PhysicalKey;
import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.buffer.DoubleTimeIndexDataBuffer;

public class BufferManager {
	private Map<PhysicalKey, DoubleTimeIndexDataBuffer> bufferMap;
	private Timer timer;

	public BufferManager() {
		this.bufferMap = new HashMap<PhysicalKey, DoubleTimeIndexDataBuffer>();
	}
	
	public boolean createBuffer(PhysicalKey key,
			int capacity, int timeUnits, int interval) {
		if(bufferMap.containsKey(key)) {
			return false;
		} 
		DoubleTimeIndexDataBuffer buffer =
				new DoubleTimeIndexDataBuffer(capacity, timeUnits, interval);
		
		bufferMap.put(key, buffer);
		timer.scheduleAtFixedRate(buffer.getIndexer(), 1000, buffer.getInterval());
		return true;
	}
	
	public void addData(PhysicalKey key, int time, short value) throws IllegalArgumentException {
		if(!bufferMap.containsKey(key)) {
			throw new IllegalArgumentException("Insert into a buffer don't exist:" + key);
		}
		
		bufferMap.get(key).addElement(time, value);
	}
	
	public List<DataPoint> getData(PhysicalKey key, int units) {
		if(!bufferMap.containsKey(key)) {
			throw new IllegalArgumentException("Fetch from a buffer don't exist:" + key);
		}
		
		return bufferMap.get(key).readDataPoint(units);
	}
	
	/**
	 * Clean the direct buffer foot print.
	 */
	public void clean() {
		timer.cancel();
		bufferMap.clear();
	}
	
}
