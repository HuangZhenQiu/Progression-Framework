package edu.uci.eecs.wukong.framework.wkpf;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;
import edu.uci.eecs.wukong.framework.model.Component;
import edu.uci.eecs.wukong.framework.model.ComponentMap;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.framework.model.LinkTable;
import edu.uci.eecs.wukong.framework.model.WuClassModel;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.exception.PluginUninitializedException;
import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.manager.PluginManager;

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
	private Map<Integer, WuObjectModel> portToWuObjectMap;
	// Plugin Id to WuObject
	private Map<Integer, WuObjectModel> pluginIdToWuObjectMap;
	
	private DJAData djaData;
	private ComponentMap componentMap = null;
	private LinkTable linkTable = null;
	
	private PluginManager pluginManager;
	private BufferManager bufferManager;


	public WKPF(PluginManager pluginManager, BufferManager bufferManager) {
		this.wuclasses = new ArrayList<WuClassModel> ();
		this.portToWuObjectMap = new HashMap<Integer, WuObjectModel> ();
		this.pluginIdToWuObjectMap = new HashMap<Integer, WuObjectModel> ();
		this.mptn = new MPTN();
		this.mptn.register(this);
		this.djaData = new DJAData();
		this.djaData.register(this);
		this.pluginManager = pluginManager;
		this.bufferManager = bufferManager;
		// Intial default location
		this.location = "/WuKong";
	}
	
	public void start() {
		mptn.start();
	}
	
	/**
	 * Called by DJAData after remote programmed by master
	 */
	public void update(LinkTable table, ComponentMap map) {
		this.linkTable = table;
		this.componentMap = map;
		bindWuObjects();
	}
	
	/**
	 * Notify the plugin manager to create wuobjects on corresponding port 
	 */
	private void bindWuObjects() {
		Map<Byte, Short> wuclassMap = this.componentMap.getWuClassIdList(mptn.getNodeId());
		// pluginManager.bindWuObjects(wuclassMap);
	}
	
	/**
	 * 
	 * @param pluginId
	 * @param property
	 * @param value
	 */
	public void sendSetProperty(Integer pluginId, String property, Object value) {
		
		try {
			if (this.pluginIdToWuObjectMap.containsKey(pluginId)) {
				WuObjectModel wuobject = this.pluginIdToWuObjectMap.get(pluginId);
	
				int componentId = componentMap.getComponentId(wuobject.getPort(), mptn.getNodeId());
				byte propertyId = wuobject.getPropertyId(property);
				if (componentId == -1) {
					LOGGER.error("The plugin is not used in the application, can't propogate dirty message.");
					return;
				}
				
				if (propertyId == -1) {
					LOGGER.error("Not recgonized property " + property + " , can find propertyId for it");
					return;
				}
				
				if (!djaData.isReadable()) {
					LOGGER.error("Progression server in reprogramming, can send data out.");
					return;
				}
				
				List<Link> outLinks = linkTable.getOutLinks(componentId, propertyId);
				for (Link link : outLinks) {
					int destComponentId = link.getDestId();
					byte destPropertyId = link.getDestPid();
					short destWuClassId = componentMap.getWuClassId(destComponentId);
					int destNodeId = componentMap.getPrimaryEndPointNodeId(destComponentId);
					byte destPortId = componentMap.getPrimaryEndPointPortId(destComponentId);
					
					if (destWuClassId == -1 || destNodeId == -1 || destPortId == -1) {
						if (destNodeId == mptn.getNodeId()) {
							// TODO Peter Huang connect two plugins in a progression server together
						} else {
							ByteBuffer buffer = ByteBuffer.allocate(7);
							buffer.put(destPortId);
							buffer.putShort(destWuClassId);
							buffer.put(destPropertyId);
							if (value instanceof Short) {
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_SHORT);
								buffer.putShort((Short) value);
							} else if (value instanceof Boolean) {
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_BOOLEAN);
								byte val = (Boolean)value == true ? (byte)1 : (byte)0;
								buffer.put(val);
							} else if (value instanceof Byte) {
								buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_REFRESH_RATE);
								buffer.put((Byte)value);
							}
							mptn.send(destNodeId, buffer.array());
						}
					} else {
						LOGGER.error("Error in either link table or component map, can't find info for dest info for link " + link);
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
		this.portToWuObjectMap.put(new Integer(portId), wuObject);
		this.pluginIdToWuObjectMap.put(wuObject.getPluginId(), wuObject);
	}
	
	/**
	 * When remote programming started, we need to clear old WuObjects.
	 */
	public void clearWuObject() {
		this.portToWuObjectMap.clear();
		this.pluginIdToWuObjectMap.clear();
	}

	public void onWKPFGetWuClassList(byte[] message) {
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
		
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}

	public void onWKPFGetWuObjectList(byte[] message) {
		if (message.length < 2) {
			LOGGER.error("Received Corrupted Get WuObject List request.");
		}
		
		byte messageNumber = message[3];
		int totalLength = portToWuObjectMap.size() / 4 +  portToWuObjectMap.size() % 4 == 0 ? 0 : 1;
		
		if (messageNumber > totalLength) {
			LOGGER.error("Message number larger than expected.");
		}
		
		ByteBuffer buffer = null;
		int leftSize = totalLength  - messageNumber * WKPFUtil.DEFAULT_OBJECT_SIZE;
		if (leftSize >= 4) {
			buffer = ByteBuffer.allocate(6 + WKPFUtil.DEFAULT_OBJECT_SIZE * 4);
		} else {
			buffer = ByteBuffer.allocate(6 + leftSize * 4);
		}
		buffer.put(WKPFUtil.WKPF_GET_WUOBJECT_LIST_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		buffer.put(messageNumber);
		buffer.put((byte)totalLength);
		buffer.put((byte)portToWuObjectMap.size());
		for (int i = WKPFUtil.DEFAULT_OBJECT_SIZE * messageNumber + 1; // Port starts from 1
				i < WKPFUtil.DEFAULT_OBJECT_SIZE * (messageNumber + 1); i++) {
			if (i <= portToWuObjectMap.size() && portToWuObjectMap.get(i) != null) {
				WuObjectModel object = portToWuObjectMap.get(i);
				buffer.put(object.getPort());
				buffer.putShort(object.getType().getWuClassId());
				buffer.put(WKPFUtil.PLUGIN_WUCLASS_TYPE);
			}
		}
		
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}

	public void onWKPFReadProperty(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Receive messages from end points from a FBP. We need routes message to
	 * Buffer message, put it to write place either buffer or channel.
	 * 
	 */
	public void onWKPFWriteProperty(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFMonitoredData(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFSetLocation(byte[] message) {
		// First message, if offset is 0.
		int chunkSize = 0;
		if (message[1] == 0) {
			chunkSize = message[2];
			length = message[3];
			locationbuffer = new StringBuffer();
			for(int index = 4; index < chunkSize + 3; index ++){
				locationbuffer.append(message[index]);
			}
		} else {
			chunkSize = message[2];
			for(int index = 3; index < chunkSize + 3; index ++){
				locationbuffer.append(message[index]);
			}
		}
		if (message[1] + chunkSize == locationbuffer.length()) {
			location = locationbuffer.toString();
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(1);
		buffer.put(WKPFUtil.WKPF_SET_LOCATION_R);
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}

	public void onWKPFGetLocation(byte[] message) {
		ByteBuffer buffer = ByteBuffer.allocate(4 + this.location.getBytes().length);
		buffer.put(WKPFUtil.WKPF_GET_LOCATION_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		buffer.put((byte)this.location.getBytes().length);
		buffer.put(location.getBytes());
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}
	
	/**
	 * Reset DJAData Cache to further write operation
	 */
	public void onWKPFRemoteProgramOpen(byte[] message) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(WKPFUtil.WKPF_REPRG_OPEN_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		if (djaData.open()) {
			buffer.put(WKPFUtil.WKPF_REPROG_OK);
		} else {
			buffer.put(WKPFUtil.WKPF_REPROG_FAILED);
		}
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}
	
	/**
	 * Write append data into dja
	 */
	public void onWKPFRemoteProgramWrite(byte[] message) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		int position = message[4] << 8 + message[3];
		byte[] data = Arrays.copyOfRange(message, 5, message.length);
		buffer.put(WKPFUtil.WKPF_REPRG_OPEN_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		if (djaData.append(position, data)) {
			buffer.put(WKPFUtil.WKPF_REPROG_OK);
		} else {
			buffer.put(WKPFUtil.WKPF_REPROG_FAILED);
		}
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}
	
	/**
	 * Commit to close the write operation
	 */
	public void onWKPFRemoteProgramCommit(byte[] message) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(WKPFUtil.WKPF_REPRG_OPEN_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		if (djaData.commit()) {
			buffer.put(WKPFUtil.WKPF_REPROG_OK);
		} else {
			buffer.put(WKPFUtil.WKPF_REPROG_FAILED);
		}
		mptn.send(MPTNUtil.MPTN_MASTER_ID, buffer.array());
	}
}
