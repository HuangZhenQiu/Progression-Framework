package edu.uci.eecs.wukong.framework.manager;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.exception.PluginNotFoundException;
import edu.uci.eecs.wukong.framework.load.FrameworkClassLoader;
import edu.uci.eecs.wukong.framework.load.PluginClassLoader;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.plugin.Plugin;
import edu.uci.eecs.wukong.framework.plugin.PluginPropertyMonitor;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
public class PluginManager {
	private static final String PLUGIN_PATH = "edu.uci.eecs.wukong.plugin";
	private ContextManager contextManager;
	private PluginPropertyMonitor propertyMonitor;
	private Pipeline pipeline;
	private PluginClassLoader classLoader;
	private List<Plugin> plugins;
	private WKPF wkpf;
	
	public PluginManager(ContextManager contextManager, Pipeline pipeline) {
		this.contextManager = contextManager;
		this.pipeline = pipeline;
		this.propertyMonitor = new PluginPropertyMonitor(this);
		this.classLoader = new PluginClassLoader(new FrameworkClassLoader());
		this.plugins = new ArrayList<Plugin>();
	}
	
	public void init() {
		// registerPlugin(new DemoPlugin());
	}
	
	public void registerPlugin(Plugin plugin) {
		contextManager.subscribe(plugin, plugin.registerContext());
		pipeline.registerExtension(plugin.registerExtension());
	}
	
	public void registerPlugin(String name) throws Exception {
		int pos = name.lastIndexOf('.');
		String path = "";
		if (pos == -1) {
			path = PLUGIN_PATH + '.' + name;
		} else if (name.substring(0, pos).equals(PLUGIN_PATH)) {
			throw new PluginNotFoundException("Try to load from wrong plugin path.");
		}
		
		ClassLoader loader = PluginManager.class.getClassLoader();
		Class<?> c = loader.loadClass(path);
		Plugin plugin = (Plugin)c.getConstructor().newInstance();
		registerPlugin(plugin);
	}
	
	// Dirty Property Propagation
	public void updateProperty(PropertyChangeEvent event) {
		
	}
	
	public void setWKPF(WKPF wkpf) {
		this.wkpf = wkpf;
	}
}
