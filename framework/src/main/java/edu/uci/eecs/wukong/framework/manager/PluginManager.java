package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.ProgressionKey.*;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.exception.PluginNotFoundException;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.plugin.Plugin;
import edu.uci.eecs.wukong.framework.plugin.PluginPropertyMonitor;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuClassModel;
import edu.uci.eecs.wukong.framework.wkpf.Model.WuObjectModel;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginManager {
	private final static Logger LOGGER = LoggerFactory.getLogger(WKPF.class);
	private final static String PLUGIN_PATH = "edu.uci.eecs.wukong.plugin";
	private BufferManager bufferManager;
	private ContextManager contextManager;
	private PluginPropertyMonitor propertyMonitor;
	private Pipeline pipeline;
	private List<Plugin> plugins;
	private Map<Short, WuClassModel> registedClasses;
	private WKPF wkpf;
	private String[] PLUGINS = {"switcher.SwitchPlugin"};
	private int port = 1;
	
	public PluginManager(ContextManager contextManager, BufferManager bufferManager, Pipeline pipeline) {
		this.bufferManager = bufferManager;
		this.contextManager = contextManager;
		this.pipeline = pipeline;
		this.propertyMonitor = new PluginPropertyMonitor(this);
		this.registedClasses = new HashMap<Short, WuClassModel>();
		this.plugins = new ArrayList<Plugin>();
		this.wkpf = new WKPF(this, bufferManager);
	}
	
	/**
	 * Init the Wuclasses that are discoveriable through WKPF
	 * 
	 * @throws Exception
	 */
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
			
			// A temporary solution for easier mapping and deployment.
			// Create an instance for each plugin classes as hard WuClass.
			Constructor<?> constructor = c.getConstructor();
			Plugin plugin = (Plugin) constructor.newInstance();
			plugins.add(plugin);
			WuObjectModel wuObjectModel = new WuObjectModel(wuClassModel, plugin.getPluginId());
			wkpf.addWuObject(port, wuObjectModel);
			port++;
		}
		
		this.wkpf.start();
	}
	
	/**
	 * Bind wuobjects used in a FBP with meta data sent by remote programming
	 * 
	 * @param wuobjects WuObjects to bind
	 */
	public void bindWuObjects(Map<Plugin, Map<String, PhysicalKey>> pluginMeta) {
		for (Entry<Plugin, Map<String, PhysicalKey>> entry : pluginMeta.entrySet()) {
			try {
				bindPlugin(entry.getKey(), entry.getValue());
			} catch (NoSuchFieldException e) {
				LOGGER.error("Fail to bind an instance of plugin "
						+ entry.getKey().getName() + " because unrecgonized field");
			}
		}
	}
	
	/**
	 * When master do the remote programming, we need to deallocate binds from physical key to buffer
	 * for plugins, and also unregister the extensions in pipeline. 
	 */
    public void unbindWuObjects() {
    	// TODO (Peter Huang)
    	// 1. Change the binding to buffer manager
    	// 2. Unregister the extensions in pipeline
    }
	
	/**
	 * Bind a plugin instance into progression pipeline runtime.
	 * 
	 * @param plugin  the plugin instance to bind
	 * @param propertyMap  the map from property to physical key
	 * @throws NoSuchFieldException
	 */
	public void bindPlugin(Plugin plugin, Map<String,
			PhysicalKey> propertyMap) throws NoSuchFieldException {
		contextManager.subscribe(plugin, plugin.registerContext());
		pipeline.registerExtension(plugin.registerExtension());
		bindPropertyUpdateEvent(plugin);
		plugins.add(plugin);
		
		WuClassModel wclass = registedClasses.get(plugin.getName());
		WuObjectModel object = new WuObjectModel(wclass, plugin.getPluginId());
		
		// TODO (Peter Huang) Bind property Map into buffer manager
		
		wkpf.addWuObject(plugin.getPluginId(), object);
	}
	
	/**
	 * It is a function for dynamic load a plugin within class path.
	 * 
	 * @param name the plugin class name 
	 * @param appId the application id of FBP
	 * @param propertyMap  property map
	 * @throws Exception
	 */
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
		bindPlugin(plugin, propertyMap);
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
	
	/**
	 * Dirty Property Propagation by using the PropertyChangeEvent issued by JVM
	 * @param event
	 */
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
