package edu.uci.eecs.wukong.framework.extension.impl;

import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.Extension;
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
	
	public String getAppId() {
		return plugin.getAppId();
	}
}
