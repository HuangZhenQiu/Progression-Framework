package edu.uci.eecs.wukong.framework.wkpf;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;
import edu.uci.eecs.wukong.framework.buffer.ActivityUnit;
import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.buffer.BufferUnits.ByteUnit;
import edu.uci.eecs.wukong.framework.buffer.BufferUnits.ShortUnit;
import edu.uci.eecs.wukong.framework.buffer.LocationUnit;
import edu.uci.eecs.wukong.framework.buffer.ResponseUnit;
import edu.uci.eecs.wukong.framework.model.ComponentMap;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.framework.model.LinkTable;
import edu.uci.eecs.wukong.framework.model.InitValue;
import edu.uci.eecs.wukong.framework.model.InitValueTable;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.WuClassModel;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.model.WuPropertyModel;
import edu.uci.eecs.wukong.framework.model.StateModel;
import edu.uci.eecs.wukong.framework.model.MonitorDataModel;
import edu.uci.eecs.wukong.framework.prclass.PrClassInitListener;
import edu.uci.eecs.wukong.framework.state.StateUpdateListener;
import edu.uci.eecs.wukong.framework.property.Activity;
import edu.uci.eecs.wukong.framework.property.Location;
import edu.uci.eecs.wukong.framework.property.Response;
import edu.uci.eecs.wukong.framework.monitor.MonitorListener;
import edu.uci.eecs.wukong.framework.mptn.MPTN;

import com.google.common.annotations.VisibleForTesting;
/**
 * 
 * TODO (Peter Huang) use WKPFCOMM layer to simply reply message
 * 
 *
 */
public class WKPF implements WKPFMessageListener, RemoteProgrammingListener {
	private final static Logger LOGGER = LoggerFactory.getLogger(WKPF.class);
	// Multiple Protocol Transportation Network
	private MPTN mptn;
	// Location String set by master
	private String location;
	// Location Length
	private int length = 0;
	// Buffer to host data sent by master
	private StringBuffer locationbuffer;
	// Wuclass installed in progression server
	private List<WuClassModel> wuclasses;
	// Port number to WuObject
	private Map<Byte, WuObjectModel> portToWuObjectMap;
	// Message's sequence number
	private short sequence = 0;
	
	private DJAData djaData;
	private ComponentMap componentMap = null;
	private LinkTable linkTable = null;
	private InitValueTable initValues = null;
	private BufferManager bufferManager;
	private List<PrClassInitListener> listeners;
	private List<StateUpdateListener> stateListeners;
	private List<MonitorListener> monitorListeners;
	private WKPFMetrics metrics;

	@VisibleForTesting
	public WKPF(BufferManager bufferManager) {
		this(bufferManager, null);
	}
	
	public WKPF(BufferManager bufferManager, WKPFMetrics metrics) {
		this.wuclasses = new ArrayList<WuClassModel> ();
		this.portToWuObjectMap = new TreeMap<Byte, WuObjectModel> ();
		this.listeners = new ArrayList<PrClassInitListener> ();
		this.mptn = new MPTN();
		this.mptn.register(this);
		this.djaData = new DJAData();
		this.djaData.register(this);
		this.stateListeners = new ArrayList<StateUpdateListener> ();
		this.monitorListeners = new ArrayList<MonitorListener> ();
		this.bufferManager = bufferManager;
		this.metrics = metrics;
		// Intial default location
		this.location = "/WuKong";
	}
	
	public void start(StateModel model) {
		LOGGER.info("Start to initilize WPKF");
		// Update the network information
		mptn.start(model);
		bufferManager.setMPTN(mptn);
		if (model != null) {
			this.componentMap = model.getComponentMap();
			this.linkTable = model.getLinkTable();
			this.location = model.getLocation();
		}
		fireUpdateEvent();
		LOGGER.info("Finished initilize WPKF");
	}
	
	public void shutdown() {
		mptn.shutdown();
	}
	
	public void register(PrClassInitListener listener) {
		this.listeners.add(listener);
	}
	
	public void registerStateListener(StateUpdateListener listener) {
		this.stateListeners.add(listener);
	}
	
	public void registerMonitorListener(MonitorListener listener) {
		this.monitorListeners.add(listener);
	}
	
	public void registerRemoteProgrammingListener(RemoteProgrammingListener listener) {
		this.djaData.register(listener);
	}
	
	private void fireUpdateEvent() {
		for (StateUpdateListener listener : stateListeners) {
			listener.update();
		}
 	}
	
	private void fireMonitorEvent(MonitorDataModel model) {
		for (MonitorListener listener : monitorListeners) {
			listener.onMonitorMessage(model);
		}
	}
	
	/**
	 * Called by DJAData after remote programmed by master
	 */
	public void update(LinkTable table, ComponentMap map, InitValueTable initValues) {
		this.linkTable = table;
		this.componentMap = map;
		this.initValues = initValues;
		bindWuObjects();
	}
	
	/**
	 * Notify the plugin manager to create wuobjects on corresponding port 
	 */
	private void bindWuObjects() {
		// TODO (Peter Huang) Update this, if end point use short address rather than long address
		Map<Byte, Short> wuclassMap = this.componentMap.getWuClassIdList(mptn.getLongAddress());
		List<WuObjectModel> objects = new ArrayList<WuObjectModel> ();
		
		for (Entry<Byte, Short> entry : wuclassMap.entrySet()) {
			WuObjectModel object = this.portToWuObjectMap.get(entry.getKey());
			if (object != null) {
				initValue(object, initValues.getValues());
				objects.add(object);
			}
		}
		
		for (PrClassInitListener listener : listeners) {
			listener.bindPlugins(objects);
		}
		
		LOGGER.info("Finished bind plugins with " + objects.size() + " WuObjects");
	}
	
	
	private void initValue(WuObjectModel object, List<InitValue> values) {
		for (InitValue value : values) {
			LOGGER.info("bind componentId " + value.getComponentId() + " of init value" + value);
			if (componentMap.getPrimaryEndPointNodeId(value.getComponentId()) == this.getLongAddress() &&
					componentMap.getWuClassId(value.getComponentId()) == object.getType().getWuClassId()) {
				WuPropertyModel property = object.getType().getPropertyModel(value.getPropertyNumber());
				try {
					Field field = object.getPrClass().getClass().getDeclaredField(property.getName());
					field.setAccessible(true);
					if (field.getType().equals(byte.class) && value.getSize() == 1) {
						field.set(object.getPrClass(), value.getValue()[0]);
					} else if (field.getType().equals(short.class)) {
						field.set(object.getPrClass(), WKPFUtil.getBigEndianShort(value.getValue(), 0));
					} else if(field.getType().equals(int.class)) {
						// temporary solution, there is no support as int init value in master right now.
						int intValue = new Integer(WKPFUtil.getBigEndianShort(value.getValue(), 0)).intValue();
						field.set(object.getPrClass(), intValue);
					} else if (field.getType().equals(boolean.class) && value.getSize() == 1) {
						if (value.getValue()[0] == 0) {
							field.set(object.getPrClass(), false);
						} else {
							field.set(object.getPrClass(), true);
						}
					} 
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.error("Can't find field " + property.getName() +" in PrClass " + object.getPrClass().getName());
				}
			}
		}
	}
	
	public void sendGetLinkCounter(Long nest, short linkId) {
		this.sequence ++;
		ByteBuffer buffer = ByteBuffer.allocate(10);
		buffer.put(WKPFUtil.WKPF_GET_LINK_COUNTER);
		buffer.put((byte) (this.sequence % 256));
		buffer.put((byte) (this.sequence / 256));
		buffer.put((byte) (linkId % 256));
		buffer.put((byte) (linkId / 256));
		mptn.send(nest.intValue(), buffer.array());
	}
	
	/**
	 * 
	 * @param pluginId
	 * @param property
	 * @param value
	 */
	public void sendSetProperty(byte portId, String property, Object value, int length) {
		
		try {
			if (this.portToWuObjectMap.containsKey(portId)) {
				WuObjectModel wuobject = this.portToWuObjectMap.get(portId);
	
				int componentId = componentMap.getComponentId(wuobject.getPort(), mptn.getLongAddress());
				byte propertyId = wuobject.getPropertyId(property);
				if (componentId == -1) {
					LOGGER.error("The plugin is not used in the application, can't propogate dirty message.");
					metrics.setPropertyErrorCounter.inc();
					return;
				}
				
				if (propertyId == -1) {
					LOGGER.error("Not recgonized property " + property + " , can find propertyId for it");
					metrics.setPropertyErrorCounter.inc();
					return;
				}
				
				if (!djaData.isReadable()) {
					LOGGER.error("Progression server in reprogramming, can send data out.");
					metrics.setPropertyErrorCounter.inc();
					return;
				}
				
				List<Link> outLinks = linkTable.getOutLinks(componentId, propertyId);
				for (Link link : outLinks) {
					int destComponentId = link.getDestId();
					byte destPropertyId = link.getDestPid();
					short destWuClassId = componentMap.getWuClassId(destComponentId);
					long destNodeId = componentMap.getPrimaryEndPointNodeId(destComponentId);
					byte destPortId = componentMap.getPrimaryEndPointPortId(destComponentId);
					
					if (destWuClassId == -1 || destNodeId == -1 || destPortId == -1) {
						LOGGER.error("Error in either link table or component map, can't find info for dest info for link " + link);
						metrics.setPropertyErrorCounter.inc();
					} else {
						if (destNodeId == mptn.getLongAddress()) {
							// TODO Peter Huang connect two plugins in a progression server together
							LOGGER.info("Propagate a dirty value to a Wuobject in local progression server");
							WuObjectModel destObject = this.portToWuObjectMap.get(new Byte(destPortId));
							WuPropertyModel destProperty = destObject.getType().getPropertyModel(new Byte(destPropertyId));
							if (destProperty.getPtype().equals(PropertyType.Input) && destProperty.getDtype().equals(DataType.Channel)) {
								NPP npp = new NPP(destNodeId, destPortId, destPropertyId);
								bufferManager.addRealTimeData(npp, ((Integer)value).shortValue());
							} else {
								LOGGER.error("We only handle with channel type input right now.");
							}
						
						} else {
							this.sequence++;
							ByteBuffer buffer = ByteBuffer.allocate(13 + length); // include dummy piggyback count 0
							if (destNodeId == 1) {
								buffer.put(WKPFUtil.MONITORING);
							} else {
								buffer.put(WKPFUtil.WKPF_WRITE_PROPERTY);
							}
							buffer.put((byte) (this.sequence % 256));
							buffer.put((byte) (this.sequence / 256));
							buffer.put(destPortId);
							buffer.putShort(destWuClassId);
							buffer.put(destPropertyId);
							if (value instanceof Integer) {
								Integer val = (Integer) value;
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_SHORT);
								buffer.putShort(val.shortValue());
							} else if (value instanceof Short) {
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_SHORT);
								buffer.putShort((Short) value);
							} else if (value instanceof Boolean) {
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_BOOLEAN);
								byte val = (Boolean)value == true ? (byte)1 : (byte)0;
								buffer.put(val);
							} else if (value instanceof Byte) {
								// Problems: where is value? Refresh_rate should be SHORT which is the same instanceof WKPF_PROPERTY_TYPE_SHORT
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_REFRESH_RATE);
								buffer.putShort((Short)value);
							} else if (value instanceof Location) {
								Location location = (Location) value;
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_LOCATION);
								buffer.put((new LocationUnit(location)).toArray());
								buffer.putInt(location.getSequence());
							} else if (value instanceof Activity) {
								Activity activity = (Activity) value;
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_ACTIVITY);
								buffer.put(new ActivityUnit(activity).toArray());
								buffer.putInt(activity.getSequence());
							} else if (value instanceof Response) {
								Response response = (Response) value;
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_RESPONSE);
								buffer.putInt(response.getSequence());
							}
							
							//TODO (Peter Huang) add token mechanism designed by sen.
							buffer.put((byte) (link.getSourceId() / 256));
							buffer.put((byte) (link.getSourceId() % 256));
							buffer.put((byte) (link.getDestId() / 256));
							buffer.put((byte) (link.getDestId() % 256));
							buffer.put((byte) 0);
							
							mptn.send((int)destNodeId, buffer.array());
							metrics.setPropertyCounter.inc();
							LOGGER.info("Send set property message to destination : " + destNodeId + " with data " + MPTNUtil.toHexString(buffer.array()));

						}
					}
				}
				
				
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}
	
	public void addWuClass(WuClassModel wuClass) {
		this.wuclasses.add(wuClass);
	}
	
	public void addWuObject(byte portId, WuObjectModel wuObject) {
		this.portToWuObjectMap.put(portId, wuObject);
	}
	
	/**
	 * When remote programming started, we need to clear old WuObjects.
	 */
	public void clearWuObject() {
		this.portToWuObjectMap.clear();
	}

	public void onWKPFGetWuClassList(long sourceId, byte[] message) {
		// TODO Auto-generated method stub
		if (message.length < 2) {
			LOGGER.error("Received Corrupted Get Wuclass List request.");
		}
		
		int messageNumber = (int)message[3];
		int totalLength = wuclasses.size() / 4 +  wuclasses.size() % 4 == 0 ? 0 : 1;
		
		if (messageNumber > totalLength) {
			LOGGER.error("Message number larger than expected.");
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(6 + WKPFUtil.DEFAULT_CLASS_SIZE * 3);
		buffer.put(WKPFUtil.WKPF_GET_WUCLASS_LIST_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		buffer.put(WKPFUtil.DEFAULT_CLASS_SIZE);
		buffer.put((byte)totalLength);
		// It is because reply.payload[5:];
		buffer.put((byte) 0);
		for (int i = WKPFUtil.DEFAULT_CLASS_SIZE * messageNumber;
				i < WKPFUtil.DEFAULT_CLASS_SIZE * (messageNumber + 1); i++) {
			if (i < wuclasses.size()) {
				buffer.putShort(wuclasses.get(i).getWuClassId());
				buffer.put(WKPFUtil.PLUGIN_WUCLASS_TYPE);
			}
		}
		
		metrics.getWuClassCounter.inc();
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}

	public void onWKPFGetWuObjectList(long sourceId, byte[] message) {
		if (message.length < 2) {
			LOGGER.error("Received Corrupted Get WuObject List request.");
		}
		
		byte messageNumber = message[3]; // payload[0]
		int wuobjectNumber = portToWuObjectMap.size(); // number_of_wuobject_messages
		int totalLength = (wuobjectNumber - 1) / WKPFUtil.DEFAULT_OBJECT_NUMBER + 1;
		
		if (messageNumber > totalLength) {
			LOGGER.error("Message number larger than expected.");
		} else {
			LOGGER.info("Message number is: " + messageNumber);
		}

		int startAtWuobjectIndex = messageNumber * WKPFUtil.DEFAULT_OBJECT_NUMBER;
		ByteBuffer buffer = null;
		int leftSize = wuobjectNumber  - startAtWuobjectIndex;
		if (leftSize >= WKPFUtil.DEFAULT_OBJECT_NUMBER) {
			leftSize = WKPFUtil.DEFAULT_OBJECT_NUMBER;
		}
		buffer = ByteBuffer.allocate(3 + 3 + leftSize * WKPFUtil.DEFAULT_OBJECT_SIZE);

		buffer.put(WKPFUtil.WKPF_GET_WUOBJECT_LIST_R);
		buffer.put(message[1]);
		buffer.put(message[2]);

		buffer.put(messageNumber);
		buffer.put((byte)totalLength);
		buffer.put((byte)wuobjectNumber);
		
		List<WuObjectModel> portToWuObjectList = new ArrayList<WuObjectModel>(portToWuObjectMap.values());
		for (int i = 0; i < leftSize; ++i) {
			WuObjectModel object = portToWuObjectList.get(startAtWuobjectIndex + i);
			if (object.getType() !=null) {
				buffer.put(object.getPort());
				buffer.putShort(object.getType().getWuClassId());
				buffer.put(WKPFUtil.PLUGIN_WUCLASS_TYPE);
			}
		}
		
		metrics.getWuObjectCounter.inc();
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}

	public void onWKPFReadProperty(long sourceId, byte[] message) {
	}

	/**
	 * Receive messages from end points from a FBP. We need routes message to
	 * Buffer message, put it to write place either buffer or channel.
	 * 
	 */
	public void onWKPFWriteProperty(long sourceId, byte[] message) {
		byte port = message[3];
		short wuclassId = WKPFUtil.getBigEndianShort(message, 4);
		byte propertyId = message[6];
		byte type = message[7];
		int size = 0;
		
		WuObjectModel wuobject = portToWuObjectMap.get(Byte.valueOf(port));
		if (wuobject == null) {
			LOGGER.error("Can't find WuObject with port " + port + " for write property command.");
			return;
		}
		
		WuPropertyModel wuproperty = wuobject.getType().getPropertyModel(propertyId);
		if (wuproperty == null) {
			LOGGER.error("Can't find WuPropety with propertyId " + propertyId + " for write property command.");
			return;
		}
		
		NPP npp = new NPP(this.mptn.getLongAddress(), port, propertyId);
		int length = WKPFUtil.WKPF_WRITE_PROPERTY_LENGTH;
		if (type == WKPFUtil.WKPF_PROPERTY_TYPE_REFRESH_RATE
				|| type == WKPFUtil.WKPF_PROPERTY_TYPE_SHORT) {
			short value = (short) ((int)(message[8] & 0xff) << 8 + message[9]);
			if (wuproperty.getDtype().equals(DataType.Channel)) {
				bufferManager.addRealTimeData(npp, value);
			} else {
				bufferManager.addData(npp, System.currentTimeMillis(), new ShortUnit(value));
			}
			length += 2;
		} else if ((type == WKPFUtil.WKPF_PROPERTY_TYPE_BOOLEAN)) {
			byte value = message[8];
			if (wuproperty.getDtype().equals(DataType.Channel)) {
				bufferManager.addRealTimeData(npp, value);
			} else {
				bufferManager.addData(npp, System.currentTimeMillis(), new ByteUnit(value));
			}
			length += 1;
		} else if (type == WKPFUtil.WKPF_PROPERTY_TYPE_LOCATION) {
			LocationUnit location = new LocationUnit();
			if (message.length >= length) {
				location.parse(ByteBuffer.wrap(Arrays.copyOfRange(
						message, WKPFUtil.WKPF_WRITE_PROPERTY_LENGTH, length)), true);
				length += location.size() + 4;
				if (wuproperty.getDtype().equals(DataType.Channel)) {
					bufferManager.addRealTimeData(npp, location);
				} else {
					bufferManager.addData(npp, System.currentTimeMillis(), location);
				}
			} else {
				LOGGER.error("Broken message for writing location property into channel");
			}
		} else if (type == WKPFUtil.WKPF_PROPERTY_TYPE_ACTIVITY) {
			ActivityUnit activity = new ActivityUnit();
			if (message.length >= length) {
				activity.parse(ByteBuffer.wrap(Arrays.copyOfRange(
						message, WKPFUtil.WKPF_WRITE_PROPERTY_LENGTH, length)), true);
				length += activity.size() + 4;
				if (wuproperty.getDtype().equals(DataType.Channel)) {
					bufferManager.addRealTimeData(npp, activity);
				} else {
					bufferManager.addData(npp, System.currentTimeMillis(), activity);
				}
			} else {
				LOGGER.error("Broken message for writing activity property into channel");
			}
		} else if (type == WKPFUtil.WKPF_PROPERTY_TYPE_RESPONSE){
			ResponseUnit response = new ResponseUnit();
			if (message.length >= length) {
				response.parse(ByteBuffer.wrap(Arrays.copyOfRange(
						message, WKPFUtil.WKPF_WRITE_PROPERTY_LENGTH, length)), true);
				length += response.size() + 4;
				if (wuproperty.getDtype().equals(DataType.Channel)) {
					bufferManager.addRealTimeData(npp, response);
				} else {
					bufferManager.addData(npp, System.currentTimeMillis(), response);
				}
			} else {
				LOGGER.error("Broken message for writing activity property into channel");
			}

		} else {
			LOGGER.error("Unrecgonized write property message type " + type);
		}
		
		
		if (Configuration.getInstance().isMonitorEnabled()
				&& length > WKPFUtil.WKPF_WRITE_PROPERTY_LENGTH) {
			byte[] data = Arrays.copyOfRange(message, WKPFUtil.WKPF_WRITE_PROPERTY_LENGTH, length);
			
			MonitorDataModel model = new MonitorDataModel(
					sourceId, wuclassId, port, propertyId, type, length, data, System.currentTimeMillis());
			this.fireMonitorEvent(model);
		}
		
		//TODO (Peter Huang) return error code, when problem happens
		ByteBuffer buffer = ByteBuffer.allocate(7);
		buffer.put(WKPFUtil.WKPF_WRITE_PROPERTY_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		buffer.put(port);
		buffer.put((byte) (wuclassId % 256));
		buffer.put((byte) (wuclassId / 256));
		buffer.put(propertyId);
		
		metrics.writePropertyCounter.inc();
		mptn.send(new Long(sourceId).intValue(), buffer.array());
	}
	
	public void onWKPFRequestPropertyInit(long sourceId, byte[] message) {
		byte port = message[3];
		byte propertyId = message[4];
		
		WuObjectModel wuobject = portToWuObjectMap.get(Byte.valueOf(port));
		if (wuobject == null) {
			LOGGER.error("Can't find WuObject with port " + port + " for write property command.");
			return;
		}
		
		WuPropertyModel wuproperty = wuobject.getType().getPropertyModel(propertyId);
		if (wuproperty == null) {
			LOGGER.error("Can't find WuPropety with propertyId " + propertyId + " for write property command.");
			return;
		}
		
		//TODO (Peter Huang) return error code, when problem happens
		ByteBuffer buffer = ByteBuffer.allocate(7);
		buffer.put(WKPFUtil.WKPF_REQUEST_PROPERTY_INIT_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		buffer.put(port);
		buffer.put(propertyId);
		
		metrics.propertyInitCounter.inc();
		mptn.send(new Long(sourceId).intValue(), buffer.array());
	}

	public void onWKPFMonitoredData(long sourceId, byte[] message) {
		if (message.length >= 7) {
			short wuclassId = WKPFUtil.getBigEndianShort(message, 2);
			byte port = message[4];
			byte propertyId = message[5];
			byte type = message[6];
			int length = (message.length - WKPFUtil.WKPF_WRITE_PROPERTY_LENGTH);
			byte[] data = Arrays.copyOfRange(message, WKPFUtil.WKPF_WRITE_PROPERTY_LENGTH, length);
			
			MonitorDataModel model = new MonitorDataModel(
					sourceId, wuclassId, port, propertyId, type, length, data, System.currentTimeMillis());

			metrics.minitorCounter.inc();
			this.fireMonitorEvent(model);
		} else {
			LOGGER.error("Recevied Broken Monitoring message data = " + message);
		}
	}

	public void onWKPFSetLocation(long sourceId, byte[] message) {
		// First message, if offset is 0.
		int chunkSize = 0;
		if (message[3] == 0) {
			chunkSize = message[4];
			length = message[5];
			locationbuffer = new StringBuffer();
			for(int index = 6; index < chunkSize + 6; index ++){
				if (index < message.length) {
					locationbuffer.append((char)message[index]);
				}
			}
		} else {
			chunkSize = message[4];
			for(int index = 5; index < chunkSize + 5; index ++){
				if (index < message.length) {
					locationbuffer.append((char)message[index]);
				}
			}
		}
		if (length == locationbuffer.length()) {
			location = locationbuffer.toString();
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(1);
		buffer.put(WKPFUtil.WKPF_SET_LOCATION_R);
		metrics.setLocationCounter.inc();
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}

	public void onWKPFGetLocation(long sourceId, byte[] message) {
		ByteBuffer buffer = ByteBuffer.allocate(4 + this.location.getBytes().length);
		buffer.put(WKPFUtil.WKPF_GET_LOCATION_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		buffer.put((byte)this.location.getBytes().length);
		buffer.put(location.getBytes());
		metrics.getLocationCounter.inc();
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}
	
	/**
	 * Reset DJAData Cache to further write operation
	 * Return message format
	 * [0] WKPF_REPRG_OPEN_R
	 * [1] sequence % 256
	 * [2] sequence / 256
	 * [3] WKPF_REPROG_OK | WKPF_REPRG_FAILED
	 * [4] pagesize & 256
	 * [5] pagesize /256
	 */
	public void onWKPFRemoteProgramOpen(long sourceId, byte[] message) {
		ByteBuffer buffer = ByteBuffer.allocate(6);
		buffer.put(WKPFUtil.WKPF_REPRG_OPEN_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		// java byte is signed
		int totalSize = WKPFUtil.getUnsignedByteValue(message[4]) * 256 + WKPFUtil.getUnsignedByteValue(message[3]);
		if (djaData.open(totalSize)) {
			buffer.put(WKPFUtil.WKPF_REPROG_OK);
		} else {
			buffer.put(WKPFUtil.WKPF_REPROG_FAILED);
		}
		buffer.put((byte) (DJAData.DEFAULT_PAGE_SIZE % 256));
		buffer.put((byte) (DJAData.DEFAULT_PAGE_SIZE / 256));
		
		metrics.reprogramOpenCounter.inc();
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}
	
	/**
	 * Write append data into dja
	 */
	public void onWKPFRemoteProgramWrite(long sourceId, byte[] message) {
		ByteBuffer buffer = ByteBuffer.allocate(5);
		// java byte is signed
		int position = WKPFUtil.getUnsignedByteValue(message[4]) * 256 + WKPFUtil.getUnsignedByteValue(message[3]);
		byte[] data = Arrays.copyOfRange(message, 5, message.length);
		buffer.put(WKPFUtil.WKPF_REPRG_WRITE_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		if (djaData.append(position, data)) {
			buffer.put(WKPFUtil.WKPF_REPROG_OK);
		} else {
			buffer.put(WKPFUtil.WKPF_REPROG_FAILED);
		}
		
		metrics.reprogramWriteCounter.inc();
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}
	
	/**
	 * Commit to close the write operation
	 */
	public void onWKPFRemoteProgramCommit(long sourceId, byte[] message) {
		ByteBuffer buffer = ByteBuffer.allocate(5);
		buffer.put(WKPFUtil.WKPF_REPRG_COMMIT_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		
		if (djaData.commit()) {
			buffer.put(WKPFUtil.WKPF_REPROG_OK);
		} else {
			buffer.put(WKPFUtil.WKPF_REPROG_FAILED);
		}
		
		metrics.reprogramCommitCounter.inc();
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
		djaData.fireUpdateEvent();
	}
	
	public void onWKPFLinkCounterReturn(long sourceId, byte[] message) {
		short linkId = WKPFUtil.getBigEndianShort(message, 3);
		short count = WKPFUtil.getBigEndianShort(message, 5);
		this.linkTable.setCounter(linkId, count);
		LOGGER.info(String.format("Received count %d for link %d.", count, linkId));
	}
	
	public int getNetworkId() {
		return this.mptn.getNodeId();
	}
	
	public long getLongAddress() {
		return this.mptn.getLongAddress();
	}
	
	public String getUUID() {
		if (this.mptn.getUuid() != null) {
			return new String(this.mptn.getUuid());
		}
		return "";
	}
	
	public LinkTable getLinkTable() {
		return this.linkTable;
	}
	
	public ComponentMap getComponentMap() {
		return this.componentMap;
	}
	
	public String getLocation() {
		return this.location;
	}
}
