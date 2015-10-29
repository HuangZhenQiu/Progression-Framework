package edu.uci.eecs.wukong.framework.model;

import edu.uci.eecs.wukong.framework.wkpf.WKPF;

public class StateModel {
	private int nodeId;
	private long longAddress;
	private LinkTable linkTable;
	private ComponentMap componentMap;
	
	public StateModel(WKPF wkpf) {
		this.nodeId = wkpf.getNetworkId();
		this.longAddress = wkpf.getLongAddress();
		this.linkTable = wkpf.getLinkTable();
		this.componentMap = wkpf.getComponentMap();
	}
}
