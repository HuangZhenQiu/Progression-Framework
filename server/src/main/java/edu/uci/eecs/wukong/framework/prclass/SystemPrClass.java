package edu.uci.eecs.wukong.framework.prclass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.uci.eecs.wukong.framework.model.Component;
import edu.uci.eecs.wukong.framework.model.ComponentMap;
import edu.uci.eecs.wukong.framework.model.InitValueTable;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.framework.model.LinkTable;
import edu.uci.eecs.wukong.framework.wkpf.RemoteProgrammingListener;

/**
 * SystemPrClass provides an interface for managing FBP with monitoring capability.
 * 
 * @author peter huang
 * 
 */
public abstract class SystemPrClass extends EdgePrClass implements RemoteProgrammingListener  {
	protected boolean enabled;
	protected String appId;
	protected LinkTable linkTable;
	protected ComponentMap map;
	protected InitValueTable initValues;
	protected Poller poller;
	
	public SystemPrClass(String name, Poller poller, PrClassMetrics metrics) {
		super(name, PrClass.PrClassType.SYSTEM_PRCLASS, metrics);
		this.poller = poller;
		this.enabled = false;
	}
	
	public void update(LinkTable table, ComponentMap map, InitValueTable initValues, String appId) {
		this.linkTable = table;
		this.map = map;
		this.initValues = initValues;
		this.appId = appId;
	}
	
	public Iterator<Link> getLinkIterator() {
		return this.linkTable.getLinkIterator();
	}
	
	public List<Long> getAllComponentAddress() {
		List<Long> addresses = new ArrayList<Long> ();
		for (int i = 0; i < map.length(); i++){
			addresses.add(getComponentAddress(i));
		}
		
		return addresses;
	}
	
	public Long getComponentAddress(int componentId) {
		Component component = this.map.getComponentById(componentId);
		if (component != null) {
			return component.getPrimaryEndPoint().getNodeId();
		} else {
			return -1L;
		}
	}
	
	public Poller getPoller() {
		return this.poller;
	}
	
	public InitValueTable getInitValueTable() {
		return this.initValues;
	}
}
