package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public abstract class AbstractExtension implements Extension {
	protected PrClass plugin;
	public AbstractExtension(PrClass plugin) {
		this.plugin = plugin;
	}
	
	public void setup() {
		
	}
	
	public void clean(ExecutionContext context) {
		
	}

	public PrClass getPlugin() {
		return plugin;
	}

	public void setPlugin(PrClass plugin) {
		this.plugin = plugin;
	}
	
	public byte getPortId() {
		return plugin.getPortId();
	}
}
