package edu.uci.eecs.wukong.framework.manager;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.lang.IllegalArgumentException;

import edu.uci.eecs.wukong.framework.ProgressionKey.PhysicalKey;
import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.buffer.DoubleTimeIndexDataBuffer;
import edu.uci.eecs.wukong.framework.channel.Channel;

public class BufferManager {
	private Map<PhysicalKey, DoubleTimeIndexDataBuffer<?>> bufferMap;
	private Map<PhysicalKey, Channel<?>> channelMap;
	private Timer timer;

	public BufferManager() {
		this.bufferMap = new HashMap<PhysicalKey, DoubleTimeIndexDataBuffer<?>>();
		this.channelMap = new HashMap<PhysicalKey, Channel<?>>();
	}
	
	public boolean createByteChannel(PhysicalKey key) {
		if(channelMap.containsKey(key)) {
			return false;
		}
		Channel<Byte> channel = new Channel<Byte>(key);
		channelMap.put(key, channel);
		
		return true;
	}
	
	public boolean createByteBuffer(PhysicalKey key,
			int capacity, int timeUnits, int interval) {
		if(bufferMap.containsKey(key)) {
			return false;
		} 
		DoubleTimeIndexDataBuffer<Byte> buffer =
				new DoubleTimeIndexDataBuffer<Byte>(capacity, timeUnits, interval);
		
		bufferMap.put(key, buffer);
		timer.scheduleAtFixedRate(buffer.getIndexer(), 1000, buffer.getInterval());
		return true;
	}
	
	public boolean createShortBuffer(PhysicalKey key,
			int capacity, int timeUnits, int interval) {
		if(bufferMap.containsKey(key)) {
			return false;
		} 
		DoubleTimeIndexDataBuffer<Short> buffer =
				new DoubleTimeIndexDataBuffer<Short>(capacity, timeUnits, interval);
		
		bufferMap.put(key, buffer);
		timer.scheduleAtFixedRate(buffer.getIndexer(), 1000, buffer.getInterval());
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void addRealTimeData(PhysicalKey key, short value) {
		if(!channelMap.containsKey(key)) {
			throw new IllegalArgumentException("Insert into a chanel don't exist:" + key);
		}

		Channel<Short> channel = (Channel<Short>)channelMap.get(key);
		channel.append(value);
	}
	
	@SuppressWarnings("unchecked")
	public void addData(PhysicalKey key, int time, short value) throws IllegalArgumentException {
		if(!bufferMap.containsKey(key)) {
			throw new IllegalArgumentException("Insert into a buffer don't exist:" + key);
		}
		
		DoubleTimeIndexDataBuffer<Short> buffer = (DoubleTimeIndexDataBuffer<Short>) bufferMap.get(key);
		buffer.addElement(time, value);
	}
	
	public List<DataPoint<Short>> getData(PhysicalKey key, int units) {
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
		channelMap.clear();
	}
	
}
