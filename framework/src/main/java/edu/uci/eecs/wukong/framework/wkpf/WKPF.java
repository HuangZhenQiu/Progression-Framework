package edu.uci.eecs.wukong.framework.wkpf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.util.MPTNUtil;
import edu.uci.eecs.wukong.framework.util.WKPFUtil;
import edu.uci.eecs.wukong.framework.wkpf.Model.LinkNode;
import edu.uci.eecs.wukong.framework.wkpf.Model.LinkTable;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuClassModel;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuObjectModel;
import edu.uci.eecs.wukong.framework.exception.PluginUninitializedException;
import edu.uci.eecs.wukong.framework.manager.PluginManager;

public class WKPF implements WKPFMessageListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(WKPF.class);
	private MPTN mptn;
	private String location;
	private StringBuffer locationbuffer;
	private List<WuClassModel> wuclasses;
	private Map<Integer, WuObjectModel> wuobjects;  // Port number to Wuobject;
	private Map<WuObjectModel, LinkTable> linkMap;
	private PluginManager pluginManager;
	private int locationLength = 0;

	public WKPF(PluginManager pluginManager) {
		this.wuclasses = new ArrayList<WuClassModel>();
		this.wuobjects = new HashMap<Integer, WuObjectModel>();
		this.linkMap = new HashMap<WuObjectModel, LinkTable>();
		this.mptn = new MPTN();
		this.mptn.addWKPFMessageListener(this);
		this.pluginManager = pluginManager;
		this.location = "/WuKong";
	}
	
	public void start() {
		mptn.start();
	}
	
	private LinkNode getLinkNode(Integer pluginId, String property) throws Exception {
		WuObjectModel wuobject = wuobjects.get(pluginId);
		LinkTable map = linkMap.get(wuobject);
		if (map == null) {
			throw new PluginUninitializedException("Pugin " + pluginId + " is not initalized");
		}
		
		LinkNode node = map.getDestination(property);
		return node;
	}
	
	public void sendSetPropertyShort(Integer pluginId, String property, short value) {
		
		try {
			LinkNode node = getLinkNode(pluginId, property);
			if (node != null) { 
				ByteBuffer buffer = ByteBuffer.allocate(20);
				buffer.put(node.getPortId());
				buffer.putShort(node.getClassId());
				buffer.put(node.getPropertyId());
				buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_SHORT);
				buffer.putShort(value);
				mptn.send(node.getNodeId(), buffer.array());
			} else {
				LOGGER.error("Plugin " + pluginId.toString() + " property " + property + "didn't bind to any destination.");
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}
	
	public void sendSetPropertyBoolean(Integer pluginId, String property, boolean value) {
		try {
			LinkNode node = getLinkNode(pluginId, property);
			if (node != null) {
				ByteBuffer buffer = ByteBuffer.allocate(20);
				buffer.put(node.getPortId());
				buffer.putShort(node.getClassId());
				buffer.put(node.getPropertyId());
				buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_BOOLEAN);
				byte val = value == true ? (byte)1 : (byte)0;
				buffer.put(val);
				mptn.send(node.getNodeId(), buffer.array());
			}  else {
				LOGGER.error("Plugin " + pluginId.toString() + " property " + property + "didn't bind to any destination.");
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}
	
	public void sendSetPropertyRefreshRate(Integer pluginId, String property, byte value) {
		try {
			LinkNode node = getLinkNode(pluginId, property);
			if (node != null) {
				ByteBuffer buffer = ByteBuffer.allocate(20);
				buffer.put(node.getPortId());
				buffer.putShort(node.getClassId());
				buffer.put(node.getPropertyId());
				buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_REFRESH_RATE);
				buffer.put(value);
				mptn.send(node.getNodeId(), buffer.array());
			}  else {
				LOGGER.error("Plugin " + pluginId.toString() + " property " + property + "didn't bind to any destination.");
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}

	
	public void addWuClass(WuClassModel wuClass) {
		this.wuclasses.add(wuClass);
	}
	
	public void addWuObject(Integer pluginId, WuObjectModel wuObject, LinkTable linkTable) {
		this.wuobjects.put(pluginId, wuObject);
		this.linkMap.put(wuObject, linkTable);
	}

	public void onWKPFRemoteProgram(byte[] message) {
		// TODO Auto-generated method stub
		
	}

	public void onWKPFGetWuClassList(byte[] message) {
		// TODO Auto-generated method stub
		if (message.length < 2) {
			LOGGER.error("Received Corrupted Get Wuclass List request.");
		}
		
		int messageNumber = (int)message[1];
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
		buffer.put((byte)0); // Just is a padding
		
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
			LOGGER.error("Received Corrupted Get Wuclass List request.");
		}
		
		int messageNumber = (int)message[1];
		int totalLength = wuobjects.size() / 4 +  wuobjects.size() % 4 == 0 ? 0 : 1;
		
		if (messageNumber > totalLength) {
			LOGGER.error("Message number larger than expected.");
		}
		
		ByteBuffer buffer = ByteBuffer.allocate(5 + WKPFUtil.DEFAULT_OBJECT_SIZE * 4);
		buffer.put(WKPFUtil.WKPF_GET_WUOBJECT_LIST_R);
		buffer.put(message[1]);
		buffer.put(message[2]);
		buffer.put((byte)totalLength);
		buffer.put(WKPFUtil.DEFAULT_OBJECT_SIZE);
		buffer.put((byte)0); // Just is a padding
		for (int i = WKPFUtil.DEFAULT_OBJECT_SIZE * messageNumber;
				i < WKPFUtil.DEFAULT_OBJECT_SIZE * (messageNumber + 1); i++) {
			if (i < wuobjects.size()) {
				WuObjectModel object = wuobjects.get(i);
				buffer.put(object.getPort());
				buffer.putShort(wuclasses.get(i).getWuClassId());
				buffer.put(WKPFUtil.PLUGIN_WUCLASS_TYPE);
			}
		}
		
	}

	public void onWKPFReadProperty(byte[] message) {
		// TODO Auto-generated method stub
		
	}

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
			locationLength = message[3];
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
}
