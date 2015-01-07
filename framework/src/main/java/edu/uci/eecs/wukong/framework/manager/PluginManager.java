package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.Plugin;
import edu.uci.eecs.wukong.plugin.demo.DemoPlugin;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
public class PluginManager {
	private ContextManager contextManager;
	private Pipeline pipeline;
	
	public PluginManager(ContextManager contextManager, Pipeline pipeline) {
		this.contextManager = contextManager;
		this.pipeline = pipeline;
	}
	
	public void init() {
		registerPlugin(new DemoPlugin());
	}
	
	public void registerPlugin(Plugin plugin) {
		contextManager.subscribe(plugin, plugin.registerContext());
		pipeline.registerExtension(plugin.registerExtension());
	}
}
