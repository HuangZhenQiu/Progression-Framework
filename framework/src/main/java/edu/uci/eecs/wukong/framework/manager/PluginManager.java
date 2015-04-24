package edu.uci.eecs.wukong.framework.manager;

import java.beans.PropertyChangeEvent;

import edu.uci.eecs.wukong.plugin.demo.DemoPlugin;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.plugin.Plugin;
import edu.uci.eecs.wukong.framework.plugin.PluginPropertyMonitor;
public class PluginManager {
	private ContextManager contextManager;
	private PluginPropertyMonitor propertyMonitor;
	private Pipeline pipeline;
	
	public PluginManager(ContextManager contextManager, Pipeline pipeline) {
		this.contextManager = contextManager;
		this.pipeline = pipeline;
		this.propertyMonitor = new PluginPropertyMonitor(this);
	}
	
	public void init() {
		registerPlugin(new DemoPlugin());
	}
	
	public void registerPlugin(Plugin plugin) {
		contextManager.subscribe(plugin, plugin.registerContext());
		pipeline.registerExtension(plugin.registerExtension());
	}
	
	public void registerPlugin(String name) {
		
	}
	
	// Dirty Property Propagation
	public void updateProperty(PropertyChangeEvent event) {
		
	}
}
