package edu.uci.eecs.wukong.framework.buffer;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.IllegalArgumentException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.api.metrics.Gauge;
import edu.uci.eecs.wukong.framework.buffer.BufferUnits.ByteUnit;
import edu.uci.eecs.wukong.framework.buffer.BufferUnits.ShortUnit;
import edu.uci.eecs.wukong.framework.channel.Channel;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.WuClassModel;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.model.WuPropertyModel;
import edu.uci.eecs.wukong.framework.mptn.MPTN;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

import com.google.common.annotations.VisibleForTesting;
public class BufferManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(BufferManager.class);
	// Map network port property to buffer
	private Map<NPP, DoubleTimeIndexDataBuffer<?, ?>> bufferMap;
	// Map network port property to channel
	private Map<NPP, Channel<?>> channelMap;
	// Timer to set index for buffer
	private Timer timer;
	private MPTN mptn;
	private BufferMetrics metrics;

	@VisibleForTesting
	public BufferManager() {
		this(null);
	}
	
	public BufferManager(BufferMetrics metrics) {
		this.bufferMap = new HashMap<NPP, DoubleTimeIndexDataBuffer<?, ?>>();
		this.channelMap = new HashMap<NPP, Channel<?>>();
		this.timer = new Timer();
		this.metrics = metrics;
		this.timer.schedule(new BufferMetricsTask(), 0, 10 * 1000);
	}
	
	public void setMPTN(MPTN mptn) {
		this.mptn = mptn;
	}
	
	private class BufferMetricsTask extends TimerTask {

		@Override
		public void run() {
			int totalSize = 0;
			for (NPP npp : bufferMap.keySet()) {
				Gauge<Integer> gauge = metrics.getBufferSizeGauge(npp);
				totalSize += bufferMap.get(npp).getSize();
				gauge.set(bufferMap.get(npp).getSize());
			}
			
			metrics.bufferTotalSize.set(totalSize);
		}
	}
	
	public void bind(WuObjectModel model) {
		if (model.isValid()) {
			WuClassModel classModel = model.getType();
			if (classModel.getType().equals(PrClass.PrClassType.PIPELINE_PRCLASS)) {
				PipelinePrClass prClass =  (PipelinePrClass) model.getPrClass();
				for (WuPropertyModel property : classModel.getProperties()) {
					// Use long address
					NPP npp = new NPP(mptn.getLongAddress(), model.getPort(), property.getId());
					if (property.getPtype().equals(PropertyType.Input)
							&&property.getDtype().equals(DataType.Channel)) {
						for (Extension extension : prClass.registerExtension()) {
							if (extension instanceof Channelable) {
								
								this.createShortChannel(npp);
								Channelable channelable = (Channelable)extension;
								this.addChannelListener(npp, channelable);
								LOGGER.info("Added channel for PrClass " + classModel.getWuClassId());
							} else {
								LOGGER.error("PrClass define input property "
										+ property.getName()
										+ " with type channle, but didn't implement Channelable interface");
							}
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
			} else if (classModel.getType().equals(PrClass.PrClassType.SIMPLE_PRCLASS)) {
				for (WuPropertyModel property : classModel.getProperties()) {
					NPP npp = new NPP(mptn.getLongAddress(), model.getPort(), property.getId());
					this.createShortChannel(npp);
					if (property.getPtype().equals(PropertyType.Input)) {
						this.addChannelFieldHook(npp, property.getName(), model);
					} 					
				}
			} else if (classModel.getType().equals(PrClass.PrClassType.SYSTEM_PRCLASS)) {
				
			}
		}
	}
	
	public void unbind(WuObjectModel model) {
		if (model.isValid()) {
			WuClassModel classModel = model.getType();
			if (classModel.getType().equals(PrClass.PrClassType.PIPELINE_PRCLASS)) {
				PipelinePrClass prClass =  (PipelinePrClass) model.getPrClass();
				for (WuPropertyModel property : classModel.getProperties()) {
					NPP npp = new NPP(mptn.getLongAddress(), model.getPort(), property.getId());
					if (property.getPtype().equals(PropertyType.Input)
							&&property.getDtype().equals(DataType.Channel)) {
						
						for (Extension extension : prClass.registerExtension()) {
							if (extension instanceof Channelable) {
								this.removeChannelListener(npp, (Channelable) extension);
							}
						}
					} else if (property.getPtype().equals(PropertyType.Input)
							&& property.getDtype().equals(DataType.Buffer)) {
						// TODO (Peter Huang) reallocate buffer memory
					}
				}
			} else if (classModel.getType().equals(PrClass.PrClassType.SIMPLE_PRCLASS)) {
				for (WuPropertyModel property : classModel.getProperties()) {
					NPP npp = new NPP(mptn.getLongAddress(), model.getPort(), property.getId());
					if (property.getPtype().equals(PropertyType.Input)) {
						this.removeChannelFieldHool(npp, property.getName(), model);
					} 
				}
			} else if (classModel.getType().equals(PrClass.PrClassType.SYSTEM_PRCLASS)) {
				
			}
		}
	}
	
	private boolean createByteBuffer(NPP key,
			int capacity, int timeUnits, int interval) {
		if(bufferMap.containsKey(key)) {
			return false;
		} 
		DoubleTimeIndexDataBuffer<Byte, ByteUnit> buffer =
				new DoubleTimeIndexDataBuffer<Byte, ByteUnit>(key, ByteUnit.class, capacity, timeUnits, interval);
		
		bufferMap.put(key, buffer);
		timer.scheduleAtFixedRate(buffer.getIndexer(), 1000, buffer.getInterval());
		metrics.bufferCounter.set(bufferMap.size());
		LOGGER.info("Created Byte Buffer with key : " + key);
		return true;
	}
	
	private boolean createShortBuffer(NPP key,
			int capacity, int timeUnits, int interval) {
		if(bufferMap.containsKey(key)) {
			return false;
		} 
		DoubleTimeIndexDataBuffer<Short, ShortUnit> buffer =
				new DoubleTimeIndexDataBuffer<Short, ShortUnit>(key, ShortUnit.class, capacity, timeUnits, interval);
		
		bufferMap.put(key, buffer);
		timer.scheduleAtFixedRate(buffer.getIndexer(), 1000, buffer.getInterval());
		metrics.bufferCounter.set(bufferMap.size());
		LOGGER.info("Created Short Buffer with key : " + key);
		return true;
	}
	
	private boolean createShortChannel(NPP key) {
		if (channelMap.containsKey(key)) {
			return false;
		}
		
		Channel<Short> channel = new Channel<Short>(key);
		channelMap.put(key, channel);
		metrics.bufferCounter.set(channelMap.size());
		LOGGER.info("Created Short Channel with key : " + key);
		return true;
	}
	
	public boolean addChannelListener(NPP key, Channelable listener) {
		if (!channelMap.containsKey(key)) {
			return false;
		}
		
		Channel<?> channel = channelMap.get(key);
		channel.addListener(listener);
		return true;
	}
	
	public boolean addChannelFieldHook(NPP key, String fieldName, WuObjectModel model) {
		if (!channelMap.containsKey(key)) {
			return false;
		}
		
		Channel<?> channel = channelMap.get(key);
		channel.addField(fieldName, model);
		LOGGER.info("Added channel hook for " + fieldName + " PrClass " + model);
		return true;
	}
	
	public boolean removeChannelFieldHool(NPP key, String fieldName, WuObjectModel model) {
		if (!channelMap.containsKey(key)) {
			return false;
		}
		
		Channel<?> channel = channelMap.get(key);
		channel.removeField(fieldName, model);
		LOGGER.info("Removed channel for PrClass " + fieldName + " PrClass " + model);
		return true;
	}
	
	public boolean removeChannelListener(NPP key, Channelable listener) {
		if (!channelMap.containsKey(key)) {
			return false;
		}
		
		Channel<?> channel = channelMap.get(key);
		channel.removeListener(listener);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void addRealTimeData(NPP key, short value) {
		if(!channelMap.containsKey(key)) {
			LOGGER.error("Try to insert into a channel doesn't exist:" + key);
			return;
		}

		Channel<Short> channel = (Channel<Short>)channelMap.get(key);
		channel.append(value);
	}
	
	@SuppressWarnings("unchecked")
	public <T, E extends BufferUnit<T>> void addData(NPP key, long time, T value) throws IllegalArgumentException {
		if(!bufferMap.containsKey(key)) {
			LOGGER.error("Try to insert into a buffer doesn't exist:" + key);
		}
		
		DoubleTimeIndexDataBuffer<T, E> buffer = (DoubleTimeIndexDataBuffer<T, E>) bufferMap.get(key);
		buffer.addElement(time, value);
	}
	
	public <T, E extends BufferUnit<T>> List<DataPoint<T>> getData(NPP key, int units) {
		if(!bufferMap.containsKey(key)) {
			throw new IllegalArgumentException("Fetch from a buffer don't exist:" + key);
		}
		DoubleTimeIndexDataBuffer<T, E> buffer = (DoubleTimeIndexDataBuffer<T, E> )bufferMap.get(key); 
		return buffer.readDataPoint(units);
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
