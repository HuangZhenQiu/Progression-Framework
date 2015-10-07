package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public abstract class AbstractExtension implements Extension {
	protected Plugin plugin;
	public AbstractExtension(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void setup() {
		
	}
	
	public void clean(ExecutionContext context) {
		
	}

	public Plugin getPlugin() {
		return plugin;
	}

	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public byte getPortId() {
		return plugin.getPortId();
	}
}
