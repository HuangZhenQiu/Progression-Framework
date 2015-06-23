package edu.uci.eecs.wukong.framework.wkpf;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.util.WKPFUtil;
import edu.uci.eecs.wukong.framework.wkpf.Model.LinkNode;
import edu.uci.eecs.wukong.framework.wkpf.Model.LinkTable;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuClass;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuObject;
import edu.uci.eecs.wukong.framework.manager.PluginManager;

public class WKPF implements WKPFMessageListener{
	private MPTN mptn;
	private String location;
	private List<WuClass> wuclasses;
	private Map<Integer, WuObject> wuobjects;
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
		
	}

	public void onWKPFGetWuObjectList(byte[] message) {
		// TODO Auto-generated method stub
		
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
