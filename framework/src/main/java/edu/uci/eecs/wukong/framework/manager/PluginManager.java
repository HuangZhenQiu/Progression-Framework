package edu.uci.eecs.wukong.framework.manager;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import edu.uci.eecs.wukong.framework.ProgressionKey.*;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.exception.PluginNotFoundException;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.plugin.Plugin;
import edu.uci.eecs.wukong.framework.plugin.PluginPropertyMonitor;
import edu.uci.eecs.wukong.framework.wkpf.Model.LinkTable;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuClassModel;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuObjectModel;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

public class PluginManager {
	private static final String PLUGIN_PATH = "edu.uci.eecs.wukong.plugin";
	private ContextManager contextManager;
	private PluginPropertyMonitor propertyMonitor;
	private Pipeline pipeline;
	private List<Plugin> plugins;
	private Map<Short, WuClassModel> registedClasses;
	private WKPF wkpf;
	private String[] PLUGINS = {"demo.DemoPlugin", "switcher.SwitchPlugin", "test.TestPlugin"};
	
	public PluginManager(ContextManager contextManager, BufferManager bufferManager, Pipeline pipeline) {
		this.contextManager = contextManager;
		this.pipeline = pipeline;
		this.propertyMonitor = new PluginPropertyMonitor(this);
		this.registedClasses = new HashMap<Short, WuClassModel>();
		this.plugins = new ArrayList<Plugin>();
		this.wkpf = new WKPF(this, bufferManager);
	}
	
	// init the Wuclasses that are discoveriable through WKPF
	public void init() throws Exception {
		for (int i = 0; i < PLUGINS.length; i++) {
			String path = PLUGIN_PATH + '.' + PLUGINS[i];
			ClassLoader loader = PluginManager.class.getClassLoader();
			Class<?> c = loader.loadClass(path);
			Map<String, Integer> properties = new HashMap<String, Integer>();
			for (Field field : c.getDeclaredFields()) {
				String name = field.getName();
				Annotation[] annotations = field.getDeclaredAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation.annotationType().equals(WuProperty.class)) {
						WuProperty property = (WuProperty)annotation;
						properties.put(name, property.id());
					}
				}
			}
			
			WuClass wuclass = c.getAnnotation(WuClass.class);
			WuClassModel wuClassModel =  new WuClassModel(wuclass.id(), properties);
			registedClasses.put(wuclass.id(), wuClassModel);
			wkpf.addWuClass(wuClassModel);
		}
		
		this.wkpf.start();
	}
	
	/**
	 * Create wuobjects from port to Wuclass Id map.
	 * @param portToClassMap
	 */
	public void createWuObjects(Map<Byte, Short> portToClassMap) {
		
		
	}
	
	public void registerPlugin(Plugin plugin, Map<String,
			PhysicalKey> propertyMap) throws NoSuchFieldException {
		contextManager.subscribe(plugin, plugin.registerContext());
		pipeline.registerExtension(plugin.registerExtension());
		bindPropertyUpdateEvent(plugin);
		plugins.add(plugin);
		
		WuClassModel wclass = registedClasses.get(plugin.getName());
		WuObjectModel object = new WuObjectModel(wclass, plugin.getPluginId());
		
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
				if (annotation.annotationType().equals(WuProperty.class)) {
					WuProperty property = (WuProperty) annotation;
					if (property.type().equals("Output")) {
						output.add(name);
					}
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
		} else if (value instanceof Boolean) {
			wkpf.sendSetPropertyRefreshRate(plugin.getPluginId(), name, (Byte)value);
		} else if (value instanceof Integer) {
			wkpf.sendSetPropertyShort(plugin.getPluginId(), name, ((Integer)value).shortValue());
		}
	}

	public void setWKPF(WKPF wkpf) {
		this.wkpf = wkpf;
	}
}
