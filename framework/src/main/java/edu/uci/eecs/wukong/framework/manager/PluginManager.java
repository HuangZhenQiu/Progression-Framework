package edu.uci.eecs.wukong.framework.manager;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import edu.uci.eecs.wukong.framework.ProgressionKey.*;
import edu.uci.eecs.wukong.framework.annotation.Input;
import edu.uci.eecs.wukong.framework.annotation.Output;
import edu.uci.eecs.wukong.framework.annotation.WuClassID;
import edu.uci.eecs.wukong.framework.exception.PluginNotFoundException;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.plugin.Plugin;
import edu.uci.eecs.wukong.framework.plugin.PluginPropertyMonitor;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuClass;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuObject;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

public class PluginManager {
	private static final String PLUGIN_PATH = "edu.uci.eecs.wukong.plugin";
	private static Integer port = 0;
	private ContextManager contextManager;
	private PluginPropertyMonitor propertyMonitor;
	private Pipeline pipeline;
	private List<Plugin> plugins;
	private Map<String, WuClass> registedClasses;
	private WKPF wkpf;
	private String[] PLUGINS = {"demo.DemoPlugin", "switcher.SwitchPlugin"};
	
	public PluginManager(ContextManager contextManager, Pipeline pipeline) {
		this.contextManager = contextManager;
		this.pipeline = pipeline;
		this.propertyMonitor = new PluginPropertyMonitor(this);
		this.registedClasses = new HashMap<String, WuClass>();
		this.plugins = new ArrayList<Plugin>();
		this.wkpf = new WKPF(this);
	}
	
	// init the Wuclasses that are discoveriable through WKPF
	public void init() throws Exception {
		for (int i = 0; i < PLUGINS.length; i++) {
			String path = PLUGIN_PATH + '.' + PLUGINS[i];
			ClassLoader loader = PluginManager.class.getClassLoader();
			Class<?> c = loader.loadClass(path);
			Map<Integer, String> properties = new HashMap<Integer, String>();
			Integer id = 0;
			for (Field field : c.getDeclaredFields()) {
				String name = field.getName();
				Annotation[] annotations = field.getDeclaredAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation.annotationType().equals(Output.class) ||
							annotation.annotationType().equals(Input.class)) {
						properties.put(id, name);
						id ++;
					}
				}
			}
			
			WuClassID classId = c.getAnnotation(WuClassID.class);
			WuClass wuClass =  new WuClass(classId.number(), properties);
			registedClasses.put(PLUGINS[i], wuClass);
			wkpf.addWuClass(wuClass);
		}
	}
	
	public void registerPlugin(Plugin plugin, Map<String,
			PhysicalKey> propertyMap) {
		contextManager.subscribe(plugin, plugin.registerContext());
		pipeline.registerExtension(plugin.registerExtension());
		bindPropertyUpdateEvent(plugin);
		plugins.add(plugin);
		
		WuClass wclass = registedClasses.get(plugin.getName());
		WuObject object = new WuObject(wclass, port);
		wkpf.addWuObject(plugin.getPluginId(), object);
	}
	
	public void registerPlugin(String name, String appId, Map<String,
			PhysicalKey> propertyMap) throws Exception {
		int pos = name.lastIndexOf('.');
		String path = "";
		if (pos == -1) {
			path = PLUGIN_PATH + '.' + name;
		} else if (name.substring(0, pos).equals(PLUGIN_PATH)) {
			throw new PluginNotFoundException("Try to load from wrong plugin path.");
		}
		
		if (!registedClasses.containsKey(name)) {
			throw new PluginNotFoundException("Try to load unregisted plugin.");
		}
		
		ClassLoader loader = PluginManager.class.getClassLoader();
		Class<?> c = loader.loadClass(path);
		Plugin plugin = (Plugin)c.getConstructor(String.class, String.class).newInstance(name, appId);
		registerPlugin(plugin, propertyMap);
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
		Plugin plugin = (Plugin)event.getSource();
		if (value instanceof Boolean) {
			wkpf.sendSetPropertyBoolean(plugin.getPluginId(), name, (Boolean)value);
		} else if (value instanceof Byte) {
			wkpf.sendSetPropertyRefreshRate(plugin.getPluginId(), name, (Byte)value);
		} else if (value instanceof Short) {
			wkpf.sendSetPropertyShort(plugin.getPluginId(), name, (Short)value);
		}
	}
	
	public void setWKPF(WKPF wkpf) {
		this.wkpf = wkpf;
	}
}
