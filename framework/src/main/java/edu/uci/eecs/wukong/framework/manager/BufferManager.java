package edu.uci.eecs.wukong.framework.manager;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.lang.IllegalArgumentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.buffer.DoubleTimeIndexDataBuffer;
import edu.uci.eecs.wukong.framework.channel.Channel;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.WuClassModel;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.model.WuPropertyModel;
import edu.uci.eecs.wukong.framework.wkpf.MPTN;

public class BufferManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(BufferManager.class);
	// Map network port property to buffer
	private Map<NPP, DoubleTimeIndexDataBuffer<?>> bufferMap;
	// Map network port property to channel
	private Map<NPP, Channel> channelMap;
	// Timer to set index for buffer
	private Timer timer;
	
	private MPTN mptn;

	public BufferManager() {
		this.bufferMap = new HashMap<NPP, DoubleTimeIndexDataBuffer<?>>();
		this.channelMap = new HashMap<NPP, Channel>();
		this.timer = new Timer();
	}
	
	public void setMPTN(MPTN mptn) {
		this.mptn = mptn;
	}
	
	public void bind(WuObjectModel model) {
		if (model.isValid()) {
			WuClassModel classModel = model.getType();
			for (WuPropertyModel property : classModel.getProperties()) {
				NPP npp = new NPP(mptn.getNodeId(), model.getPort(), property.getId());
				if (property.getPtype().equals(PropertyType.Input)
						&&property.getDtype().equals(DataType.Channel)) {
					AbstractProgressionExtension extension = model.getPrClass().getProgressionExtension(); 
					if (extension != null && extension instanceof Channelable) {
						
						this.createShortChannel(npp);
						Channelable channelable = (Channelable)extension;
						this.addChannelListener(npp, channelable);
						LOGGER.info("Added channel for PrClass " + classModel.getWuClassId());
					} else {
						LOGGER.error("PrClass define input property "
								+ property.getName()
								+ " with type channle, but didn't implement Channelable interface");
					}
				} else if (property.getPtype().equals(PropertyType.Input)
						&& property.getDtype().equals(DataType.Buffer)) {
					if (property.getType().equals(short.class)) {
						createShortBuffer(npp, 1000, 100, 10);
					} else if (property.getType().equals(byte.class)) {
						createByteBuffer(npp, 1000, 100, 10);
					}
				}
			}
		}
	}
	
	private boolean createByteBuffer(NPP key,
			int capacity, int timeUnits, int interval) {
		if(bufferMap.containsKey(key)) {
			return false;
		} 
		DoubleTimeIndexDataBuffer<Byte> buffer =
				new DoubleTimeIndexDataBuffer<Byte>(key, capacity, timeUnits, interval);
		
		bufferMap.put(key, buffer);
		timer.scheduleAtFixedRate(buffer.getIndexer(), 1000, buffer.getInterval());
		LOGGER.info("Created Byte Buffer with key : " + key);
		return true;
	}
	
	private boolean createShortBuffer(NPP key,
			int capacity, int timeUnits, int interval) {
		if(bufferMap.containsKey(key)) {
			return false;
		} 
		DoubleTimeIndexDataBuffer<Short> buffer =
				new DoubleTimeIndexDataBuffer<Short>(key, capacity, timeUnits, interval);
		
		bufferMap.put(key, buffer);
		timer.scheduleAtFixedRate(buffer.getIndexer(), 1000, buffer.getInterval());
		LOGGER.info("Created Short Buffer with key : " + key);
		return true;
	}
	
	private boolean createShortChannel(NPP key) {
		if (channelMap.containsKey(key)) {
			return false;
		}
		
		Channel channel = new Channel(key);
		channelMap.put(key, channel);
		LOGGER.info("Created Short Channel with key : " + key);
		return true;
	}
	
	public boolean addChannelListener(NPP key, Channelable listener) {
		if (!channelMap.containsKey(key)) {
			return false;
		}
		
		Channel channel = channelMap.get(key);
		channel.addListener(listener);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void addRealTimeData(NPP key, short value) {
		if(!channelMap.containsKey(key)) {
			throw new IllegalArgumentException("Insert into a chanel don't exist:" + key);
		}

		Channel channel = (Channel)channelMap.get(key);
		channel.append(value);
	}
	
	@SuppressWarnings("unchecked")
	public void addData(NPP key, long time, short value) throws IllegalArgumentException {
		if(!bufferMap.containsKey(key)) {
			throw new IllegalArgumentException("Insert into a buffer don't exist:" + key);
		}
		
		DoubleTimeIndexDataBuffer<Short> buffer = (DoubleTimeIndexDataBuffer<Short>) bufferMap.get(key);
		buffer.addElement(time, value);
	}
	
	public List<DataPoint<Short>> getData(NPP key, int units) {
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
