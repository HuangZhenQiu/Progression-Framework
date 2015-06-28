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
import edu.uci.eecs.wukong.framework.wkpf.Model.WuClass;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuObject;
import edu.uci.eecs.wukong.framework.manager.PluginManager;

public class WKPF implements WKPFMessageListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(WKPF.class);
	private MPTN mptn;
	private String location;
	private List<WuClass> wuclasses;
	private Map<Integer, WuObject> wuobjects;  // Port number to Wuobject;
	private Map<WuObject, LinkTable> linkMap;
	private PluginManager pluginManager;

	public WKPF(PluginManager pluginManager) {
		this.wuclasses = new ArrayList<WuClass>();
		this.wuobjects = new HashMap<Integer, WuObject>();
		this.linkMap = new HashMap<WuObject, LinkTable>();
		this.mptn = new MPTN();
		this.mptn.addWKPFMessageListener(this);
		this.pluginManager = pluginManager;
		this.location = "";
	}
	
	public void start() {
		mptn.start();
	}
	
	private LinkNode getLinkNode(Integer pluginId, String property) {
		WuObject wuobject = wuobjects.get(pluginId);
		LinkTable map = linkMap.get(wuobject);
		LinkNode node = map.getDestination(property);
		return node;
	}
	
	public void sendSetPropertyShort(Integer pluginId, String property, short value) {
		LinkNode node = getLinkNode(pluginId, property); 
		ByteBuffer buffer = ByteBuffer.allocate(20);
		buffer.put(node.getPortId());
		buffer.putShort(node.getClassId());
		buffer.put(node.getPropertyId());
		buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_SHORT);
		buffer.putShort(value);
		mptn.send(node.getNodeId(), buffer.array());
	}
	
	public void sendSetPropertyBoolean(Integer pluginId, String property, boolean value) {
		LinkNode node = getLinkNode(pluginId, property);
		ByteBuffer buffer = ByteBuffer.allocate(20);
		buffer.put(node.getPortId());
		buffer.putShort(node.getClassId());
		buffer.put(node.getPropertyId());
		buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_BOOLEAN);
		byte val = value == true ? (byte)1 : (byte)0;
		buffer.put(val);
		mptn.send(node.getNodeId(), buffer.array());
	}
	
	public void sendSetPropertyRefreshRate(Integer pluginId, String property, byte value) {
		LinkNode node = getLinkNode(pluginId, property);
		ByteBuffer buffer = ByteBuffer.allocate(20);
		buffer.put(node.getPortId());
		buffer.putShort(node.getClassId());
		buffer.put(node.getPropertyId());
		buffer.put(WKPFUtil.WKPF_PROPERTY_TYPE_REFRESH_RATE);
		buffer.put(value);
		mptn.send(node.getNodeId(), buffer.array());
	}

	
	public void addWuClass(WuClass wuClass) {
		this.wuclasses.add(wuClass);
	}
	
	public void addWuObject(Integer pluginId, WuObject wuObject) {
		this.wuobjects.put(pluginId, wuObject);
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
		
		ByteBuffer buffer = ByteBuffer.allocate(4 + WKPFUtil.DEFAULT_OBJECT_SIZE * 3);
		buffer.put(WKPFUtil.WKPF_GET_WUCLASS_LIST_R);
		buffer.put(message[1]);
		buffer.put((byte)totalLength);
		buffer.put(WKPFUtil.DEFAULT_OBJECT_SIZE);
		
		for (int i = WKPFUtil.DEFAULT_OBJECT_SIZE * messageNumber;
				i < WKPFUtil.DEFAULT_OBJECT_SIZE * (messageNumber + 1); i++) {
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
		
		ByteBuffer buffer = ByteBuffer.allocate(4 + WKPFUtil.DEFAULT_OBJECT_SIZE * 4);
		buffer.put(WKPFUtil.WKPF_GET_WUOBJECT_LIST_R);
		buffer.put(message[1]);
		buffer.put((byte)totalLength);
		buffer.put(WKPFUtil.DEFAULT_OBJECT_SIZE);
		for (int i = WKPFUtil.DEFAULT_OBJECT_SIZE * messageNumber;
				i < WKPFUtil.DEFAULT_OBJECT_SIZE * (messageNumber + 1); i++) {
			if (i < wuobjects.size()) {
				WuObject object = wuobjects.get(i);
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
		// TODO Auto-generated method stub
		
	}

	public void onWKPFGetLocation(byte[] message) {
		
	}
}
