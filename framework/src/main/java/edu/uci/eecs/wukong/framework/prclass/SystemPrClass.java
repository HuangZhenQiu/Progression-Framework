package edu.uci.eecs.wukong.framework.prclass;

import edu.uci.eecs.wukong.framework.model.ComponentMap;
import edu.uci.eecs.wukong.framework.model.InitValueTable;
import edu.uci.eecs.wukong.framework.model.LinkTable;
import edu.uci.eecs.wukong.framework.wkpf.RemoteProgrammingListener;

/**
 * SystemPrClass provides an interface for managing FBP with monitoring capability.
 * 
 * 
 * 
 * @author peterhuang
 * 
 */
public abstract class SystemPrClass extends PipelinePrClass implements RemoteProgrammingListener  {
	protected boolean enabled;
	protected LinkTable linkTable;
	protected ComponentMap map;
	protected InitValueTable initValues;
	
	public SystemPrClass(String name, PrClassMetrics metrics) {
		super(name, PrClass.PrClassType.SYSTEM_PRCLASS, metrics);
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
}
