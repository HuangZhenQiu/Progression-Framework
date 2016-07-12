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
import edu.uci.eecs.wukong.framework.channel.BasicChannel;
import edu.uci.eecs.wukong.framework.channel.GlobalChannel;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.SensorType;
import edu.uci.eecs.wukong.framework.model.WKPFMessageType;
import edu.uci.eecs.wukong.framework.model.WuClassModel;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.model.WuPropertyModel;
import edu.uci.eecs.wukong.framework.mptn.UDPMPTN;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

import com.google.common.annotations.VisibleForTesting;
public class BufferManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(BufferManager.class);
	// Map network port property to buffer
	private Map<NPP, DoubleTimeIndexDataBuffer<?, ?>> bufferMap;
	// Map sensor type to system buffers
	private Map<SensorType, DoubleTimeIndexDataBuffer<?, ?>> systemBufferMap;
	// Map network port property to channel
	private Map<NPP, BasicChannel<?>> channelMap;
	// Map WKPF Message Type to channel
	private Map<WKPFMessageType, GlobalChannel<?>> globalChannelMap;
	
	// Timer to set index for buffer
	private Timer timer;
	private UDPMPTN mptn;
	private BufferMetrics metrics;

	@VisibleForTesting
	public BufferManager() {
		this(null);
	}
	
	public BufferManager(BufferMetrics metrics) {
		this.bufferMap = new HashMap<NPP, DoubleTimeIndexDataBuffer<?, ?>>();
		this.systemBufferMap = new HashMap<SensorType, DoubleTimeIndexDataBuffer<?, ?>>();
		this.channelMap = new HashMap<NPP, BasicChannel<?>>();
		this.globalChannelMap = new HashMap<WKPFMessageType, GlobalChannel<?>>();
		this.timer = new Timer();
		this.metrics = metrics;
		this.timer.schedule(new BufferMetricsTask(), 0, 10 * 1000);
	}
	
	public void setMPTN(UDPMPTN mptn) {
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
				for (WuPropertyModel property : classModel.getProperties()) {
					// Use long address
					NPP npp = new NPP(mptn.getLongAddress(), model.getPort(), property.getId());
					if (property.getPtype().equals(PropertyType.Input)
							&& property.getDtype().equals(DataType.Channel) && model.getExtensions() != null) {
						for (Extension extension : model.getExtensions()) {
							if (extension instanceof Channelable) {
								this.createChannel(npp);
								Channelable<?> channelable = (Channelable<?>)extension;
								this.addChannelListener(npp, channelable);
								LOGGER.debug("Added channel for PrClass " + classModel.getWuClassId());
							} else {
								LOGGER.error("PrClass define input property "
										+ property.getName()
										+ " with type channle, but didn't implement Channelable interface");
							}
						}
					} else if (property.getPtype().equals(PropertyType.Input)
							&& property.getDtype().equals(DataType.Buffer)) {
						if (property.getType().equals(short.class)) {
							createBuffer(npp, ShortUnit.class, 2, property.getCapacity(),
									property.getTimeUnit(), property.getInterval());
						} else if (property.getType().equals(byte.class)) {
							createBuffer(npp, ByteUnit.class, 1, property.getCapacity(),
									property.getTimeUnit(), property.getInterval());
						}
					}
				}
			} else if (classModel.getType().equals(PrClass.PrClassType.SIMPLE_PRCLASS)) {
				for (WuPropertyModel property : classModel.getProperties()) {
					NPP npp = new NPP(mptn.getLongAddress(), model.getPort(), property.getId());
					this.createChannel(npp);
					if (property.getPtype().equals(PropertyType.Input)) {
						this.addChannelFieldHook(npp, property.getName(), model);
					} 					
				}
			} else if (classModel.getType().equals(PrClass.PrClassType.SYSTEM_PRCLASS)) {
				for (WuPropertyModel property : classModel.getProperties()) {
					// Use long address
					NPP npp = new NPP(mptn.getLongAddress(), model.getPort(), property.getId());
					if (property.getPtype().equals(PropertyType.Input)
							&& property.getDtype().equals(DataType.GlobalChannel) && model.getExtensions() != null) {
						for (Extension extension : model.getExtensions()) {
							if (extension instanceof Channelable) {
								this.createGlobalChannel(property.getMtype());
								Channelable<?> channelable = (Channelable<?>)extension;
								this.addGlobalChannelListener(property.getMtype(), channelable);
								LOGGER.debug("Added channel for PrClass " + classModel.getWuClassId());
							} else {
								LOGGER.error("PrClass define input property "
										+ property.getName()
										+ " with type channle, but didn't implement Channelable interface");
							}
						}
					} else if (property.getPtype().equals(PropertyType.Input)
							&& property.getDtype().equals(DataType.SystemBuffer)) {
						createSystemBuffer(property.getStype());
					}
				}
			}
		}
	}
	
	public void unbind(WuObjectModel model) {
		if (model.isValid()) {
			WuClassModel classModel = model.getType();
			if (classModel.getType().equals(PrClass.PrClassType.PIPELINE_PRCLASS)) {
				EdgePrClass prClass =  (EdgePrClass) model.getPrClass();
				for (WuPropertyModel property : classModel.getProperties()) {
					NPP npp = new NPP(mptn.getLongAddress(), model.getPort(), property.getId());
					if (property.getPtype().equals(PropertyType.Input)
							&&property.getDtype().equals(DataType.Channel)) {
						for (Extension extension : model.getExtensions()) {
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
	
	
	/**
	 * 
	 * @param npp the key of the property
	 * @param type the type of value stored in buffer
	 * @param unitSize the length of data (short 2, boolean 1)
	 * @param dataCapacity total number of values can be stored
	 * @param timeUnits time units of the buffer time index
	 * @param interval the length of interval for each time unit (milliseconds)
	 * 
	 * @return whether the buffer is created successfully
	 */
	private <T, E extends BufferUnit<T>> boolean createBuffer(NPP key, Class<E> type, int unitSize,
			int capacity, int timeUnits, int interval) {
		if(bufferMap.containsKey(key)) {
			return false;
		} 
		
		BasicDoubleTimeIndexDataBuffer<T, E> buffer =
				new BasicDoubleTimeIndexDataBuffer<T, E>(key, type, unitSize, capacity, timeUnits, interval);
		
		bufferMap.put(key, buffer);
		timer.scheduleAtFixedRate(buffer.getIndexer(), 1000, interval);
		metrics.bufferCounter.set(bufferMap.size());
		LOGGER.debug("Created " + type.getSimpleName() + " Buffer with key : " + key);
		return true;
	}
	
	/**
	 * Create system buffer in the buffer manager. The unitSize, capacity, timeUnits and interval
	 * are predefined by system.
	 * 
	 * 
	 * @param key SensorType 
	 * @param type the buffer unit type
	 * @return
	 */
	private boolean createSystemBuffer(SensorType key) {
		if (this.systemBufferMap.containsKey(key)) {
			return false;
		}
		
		SystemDoubleTimeIndexDataBuffer buffer =
				new SystemDoubleTimeIndexDataBuffer(key, SensorDataUnit.class, 8, 10000, 1000, 1000);
		
		systemBufferMap.put(key, buffer);
		timer.scheduleAtFixedRate(buffer.getIndexer(), 1000, 1000);
		metrics.bufferCounter.set(bufferMap.size());
		LOGGER.debug("Created System Buffer with key : " + key);
		return true;
	}
	
	private <T> boolean createGlobalChannel(WKPFMessageType type) {
		if (this.globalChannelMap.containsKey(type)) {
			return false;
		}
		
		GlobalChannel<T> globalChannel = new GlobalChannel<T>(type);
		this.globalChannelMap.put(type, globalChannel);
		metrics.bufferCounter.set(channelMap.size());
		LOGGER.debug("Created Channel with wkpf message type : " + type.name());
		return true;
	}
	
	private <T> boolean createChannel(NPP key) {
		if (channelMap.containsKey(key)) {
			return false;
		}
		
		BasicChannel<T> channel = new BasicChannel<T>(key);
		channelMap.put(key, channel);
		metrics.bufferCounter.set(channelMap.size());
		LOGGER.debug("Created Channel with key : " + key);
		return true;
	}
	
	public <T> boolean addChannelListener(NPP key, Channelable<T> listener) {
		if (!channelMap.containsKey(key)) {
			return false;
		}
		
		BasicChannel<T> channel = (BasicChannel<T>)channelMap.get(key);
		channel.addListener(listener);
		return true;
	}
	
	public <T> boolean addGlobalChannelListener(WKPFMessageType type, Channelable<T> listener) {
		if (!globalChannelMap.containsKey(type)){
			return false;
		}
		GlobalChannel<T> channel = (GlobalChannel<T>)globalChannelMap.get(type);
		channel.addListener(listener);
		return true;
	}
	
	public boolean addChannelFieldHook(NPP key, String fieldName, WuObjectModel model) {
		if (!channelMap.containsKey(key)) {
			return false;
		}
		
		BasicChannel<?> channel = channelMap.get(key);
		channel.addField(fieldName, model);
		LOGGER.debug("Added channel hook for " + fieldName + " PrClass " + model);
		return true;
	}
	
	public boolean removeChannelFieldHool(NPP key, String fieldName, WuObjectModel model) {
		if (!channelMap.containsKey(key)) {
			return false;
		}
		
		BasicChannel<?> channel = channelMap.get(key);
		channel.removeField(fieldName, model);
		LOGGER.debug("Removed channel for PrClass " + fieldName + " PrClass " + model);
		return true;
	}
	
	public boolean removeChannelListener(NPP key, Channelable listener) {
		if (!channelMap.containsKey(key)) {
			return false;
		}
		
		BasicChannel<?> channel = channelMap.get(key);
		channel.removeListener(listener);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public <T> void addRealTimeData(NPP key, T value) {
		if(!channelMap.containsKey(key)) {
			LOGGER.error("Try to insert into a channel doesn't exist:" + key);
			return;
		}

		BasicChannel<T> channel = (BasicChannel<T>)channelMap.get(key);
		channel.append(value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void addGlobalRealTimeData(NPP key, WKPFMessageType type, T value) {
		if(!channelMap.containsKey(key)) {
			// LOGGER.error("Try to insert into a channel doesn't exist:" + key);
			return;
		}

		GlobalChannel<T> channel = (GlobalChannel<T>)this.globalChannelMap.get(type);
		if (channel != null) {
			channel.append(key, type, value);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T, E extends BufferUnit<T>> void addData(NPP key, long time, E value) throws IllegalArgumentException {
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
