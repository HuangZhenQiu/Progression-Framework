package edu.uci.eecs.wukong.framework.model;

import edu.uci.eecs.wukong.framework.manager.PluginManager;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

import java.util.List;

public class StateModel {
	private int nodeId;
	private long longAddress;
	private String location;
	private LinkTable linkTable;
	private ComponentMap componentMap;
	private List<WuObjectModel> bindedWuObject;
	
	public StateModel(WKPF wkpf, PluginManager manager) {
		this.nodeId = wkpf.getNetworkId();
		this.longAddress = wkpf.getLongAddress();
		this.location = wkpf.getLocation();
		this.linkTable = wkpf.getLinkTable();
		this.componentMap = wkpf.getComponentMap();
		this.bindedWuObject = manager.getBindedWuObjects();
	}

	public int getNodeId() {
		return nodeId;
	}

	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public long getLongAddress() {
		return longAddress;
	}

	public void setLongAddress(long longAddress) {
		this.longAddress = longAddress;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LinkTable getLinkTable() {
		return linkTable;
	}

	public void setLinkTable(LinkTable linkTable) {
		this.linkTable = linkTable;
	}

	public ComponentMap getComponentMap() {
		return componentMap;
	}

	public void setComponentMap(ComponentMap componentMap) {
		this.componentMap = componentMap;
	}

	public List<WuObjectModel> getBindedWuObject() {
		return bindedWuObject;
	}

	public void setBindedWuObject(List<WuObjectModel> bindedWuObject) {
		this.bindedWuObject = bindedWuObject;
	}
}
