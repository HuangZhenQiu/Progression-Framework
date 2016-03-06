package edu.uci.eecs.wukong.framework.prclass;

import java.util.Iterator;

import edu.uci.eecs.wukong.framework.model.Component;
import edu.uci.eecs.wukong.framework.model.ComponentMap;
import edu.uci.eecs.wukong.framework.model.InitValueTable;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.framework.model.LinkTable;
import edu.uci.eecs.wukong.framework.wkpf.RemoteProgrammingListener;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

/**
 * SystemPrClass provides an interface for managing FBP with monitoring capability.
 * 
 * @author peterhuang
 * 
 */
public abstract class SystemPrClass extends PipelinePrClass implements RemoteProgrammingListener  {
	protected boolean enabled;
	protected LinkTable linkTable;
	protected ComponentMap map;
	protected InitValueTable initValues;
	protected WKPF wkpf;
	
	public SystemPrClass(String name, WKPF wkpf, PrClassMetrics metrics) {
		super(name, PrClass.PrClassType.SYSTEM_PRCLASS, metrics);
		this.wkpf = wkpf;
		this.enabled = false;
	}
	
	public void remap() {
		configManager.remapping("");
	}
	
	public void update(LinkTable table, ComponentMap map, InitValueTable initValues) {
		this.linkTable = table;
		this.map = map;
		this.initValues = initValues;
	}
	
	public Iterator<Link> getLinkIterator() {
		return this.linkTable.getLinkIterator();
	}
	
	public void sendGetLinkCounter(Long address, short linkId) {
		this.wkpf.sendGetLinkCounter(address, linkId);
	}
	
	public Long getComponentAddress(int componentId) {
		Component component = this.map.getComponentById(componentId);
		if (component != null) {
			return component.getPrimaryEndPoint().getNodeId();
		} else {
			return -1L;
		}
	}
	
	public InitValueTable getInitValueTable() {
		return this.initValues;
	}
}
