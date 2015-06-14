package edu.uci.eecs.wukong.framework.manager;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import edu.uci.eecs.wukong.framework.annotation.Output;
import edu.uci.eecs.wukong.framework.exception.PluginNotFoundException;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.plugin.Plugin;
import edu.uci.eecs.wukong.framework.plugin.PluginPropertyMonitor;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
public class PluginManager {
	private static final String PLUGIN_PATH = "edu.uci.eecs.wukong.plugin";
	private ContextManager contextManager;
	private PluginPropertyMonitor propertyMonitor;
	private Pipeline pipeline;
	private List<Plugin> plugins;
	private WKPF wkpf;
	
	public PluginManager(ContextManager contextManager, Pipeline pipeline) {
		this.contextManager = contextManager;
		this.pipeline = pipeline;
		this.propertyMonitor = new PluginPropertyMonitor(this);
		this.plugins = new ArrayList<Plugin>();
	}
	
	// init the Wuclasses that are discoveriable through WKPF
	public void init() {
	}
	
	public void registerPlugin(Plugin plugin) {
		contextManager.subscribe(plugin, plugin.registerContext());
		pipeline.registerExtension(plugin.registerExtension());
		bindPropertyUpdateEvent(plugin);
		plugins.add(plugin);
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
	
	// bind the update event of out property for plugin.
	private void bindPropertyUpdateEvent(Plugin plugin) {
		List<String> output = new ArrayList<String>();
		for (Field field : plugin.getClass().getDeclaredFields()) {
			String name = field.getName();
			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(Output.class)) {
					output.add(name);
				}
			}
		}
		
		propertyMonitor.addMonitorProperty(plugin, output);
	}
	
	// Dirty Property Propagation
	public void updateProperty(PropertyChangeEvent event) {
		String name = event.getPropertyName();
		Object value = event.getNewValue();
		Object plugin = event.getSource();
	}
	
	public void setWKPF(WKPF wkpf) {
		this.wkpf = wkpf;
	}
}
