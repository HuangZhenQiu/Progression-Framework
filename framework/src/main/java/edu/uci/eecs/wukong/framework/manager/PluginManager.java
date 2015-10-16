package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.exception.PluginNotFoundException;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassInitListener;
import edu.uci.eecs.wukong.framework.prclass.PrClassPropertyMonitor;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.model.WuClassModel;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

import java.beans.PropertyChangeEvent;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginManager implements PrClassInitListener {
	private final static Logger LOGGER = LoggerFactory.getLogger(PluginManager.class);
	private final static String PLUGIN_PATH = "edu.uci.eecs.wukong.prclass";
	private BufferManager bufferManager;
	private SceneManager contextManager;
	private PrClassPropertyMonitor propertyMonitor;
	private Pipeline pipeline;
	private List<PrClass> plugins;
	/* WuclassId to WuClass model */
	private Map<Short, WuClassModel> registedClasses;
	private WKPF wkpf;
	private String[] PLUGINS = {"switcher.SwitchPrClass", "timertest.TimerPrClass", 
			"icsdemo.ICSDemoHuePrClass", "icsdemo.ICSDemoFanPrClass", 
			"icsdemo.ICSDemoAromaPrClass", "icsdemo.ICSDemoMusicPrClass", 
			"icsdemo.ICSDemoTVPrClass", "icsdemo.ICSDemoQPrClass"
		};
	
	public PluginManager(WKPF wkpf, SceneManager contextManager, Pipeline pipeline, BufferManager bufferManager) {
		this.bufferManager = bufferManager;
		this.contextManager = contextManager;
		this.pipeline = pipeline;
		this.propertyMonitor = new PrClassPropertyMonitor(this);
		this.registedClasses = new HashMap<Short, WuClassModel>();
		this.plugins = new ArrayList<PrClass>();
		this.wkpf = wkpf;
	}
	
	/**
	 * Init the Wuclasses that are able to discovered through WKPF. Currently, 
	 * we create an instance for each Wuclass for exposing Wuobject to mapping
	 * in master. But these WuObject's extensions will not be bind to pipeline
	 * until receiving reprogramming commands from master.
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
		for (int i = 0; i < PLUGINS.length; i++) {
			String path = PLUGIN_PATH + '.' + PLUGINS[i];
			ClassLoader loader = PluginManager.class.getClassLoader();
			Class<?> c = loader.loadClass(path);
			WuClassModel wuClassModel = createWuClassModel(path, c);
			
			LOGGER.info("Initialized Wuclass in progression server : " + wuClassModel);
			
			// A temporary solution for easier mapping and deployment.
			// Create an instance for each plugin classes as hard WuClass.
			Constructor<?> constructor = c.getConstructor();
			PrClass plugin = (PrClass) constructor.newInstance();
			plugins.add(plugin);
			WuObjectModel wuObjectModel = new WuObjectModel(wuClassModel, plugin);
			wkpf.addWuObject(plugin.getPortId(), wuObjectModel);
		}
	}
	
	private WuClassModel createWuClassModel(String path, Class<?> c) throws ClassNotFoundException {
		WuClass wuclass = c.getAnnotation(WuClass.class);
		if (wuclass == null) {
			LOGGER.info("Can't find WuClass annotation for PrClass " + path);
			return null;
		}
			
		WuClassModel wuClassModel =  new WuClassModel(wuclass.id());
		for (Field field : c.getDeclaredFields()) {
			String name = field.getName();
			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(WuProperty.class)) {
					WuProperty property = (WuProperty)annotation;
					wuClassModel.addProperty(name, property);
				}
			}
		}
		
		registedClasses.put(wuclass.id(), wuClassModel);
		wkpf.addWuClass(wuClassModel);
		
		return wuClassModel;
	}
	
	/**
	 * Bind wuobjects used in a FBP with meta data sent by remote programming
	 * 
	 * @param wuobjectMap map port to wuclassId
	 */
	public void bindPlugins(List<WuObjectModel> objects) {
		LOGGER.info("Start to bind plugins into plugin manager, the size of objects is" + objects.size());
		for (WuObjectModel model : objects) {
			bindPlugin(model);
		}
	}
	
	/**
	 * Bind a plugin instance into progression pipeline runtime.
	 * 
	 * @param model  the Wuobject instance to bind
	 * @param propertyMap  the map from property to physical key
	 * @throws NoSuchFieldException
	 */
	public void bindPlugin(WuObjectModel model) {
		PrClass prClass = model.getPrClass();
		contextManager.subscribe(prClass, prClass.registerContext());
		bufferManager.bind(model);
		pipeline.registerExtension(prClass.registerExtension());
		bindPropertyUpdateEvent(prClass);
		LOGGER.info("Finished bind plugin with context manager, pipeline and property monitor.");
	}
	
	/**
	 * When master do the remote programming, we need to deallocate binds from physical key to buffer
	 * for plugins, and also unregister the extensions in pipeline. 
	 */
    public void unbindPlugin() {
    	for (PrClass plugin : plugins) {
    		pipeline.unregisterExtension(plugin.registerExtension());
    	}
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
			NPP> propertyMap) throws Exception {
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
		WuClassModel wuClassModel = createWuClassModel(path, c);
		PrClass plugin = (PrClass)c.getConstructor(String.class, String.class).newInstance(name, appId);
		
		plugins.add(plugin);
		WuObjectModel wuObjectModel = new WuObjectModel(wuClassModel, plugin);
		wkpf.addWuObject(plugin.getPortId(), wuObjectModel);
		bindPlugin(wuObjectModel);
	}
	
	// bind the update event of out property for plugin.
	private void bindPropertyUpdateEvent(PrClass plugin) {
		List<String> output = new ArrayList<String>();
		for (Field field : plugin.getClass().getDeclaredFields()) {
			String name = field.getName();
			Annotation[] annotations = field.getDeclaredAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(WuProperty.class)) {
					WuProperty property = (WuProperty) annotation;
					if (property.type().equals(PropertyType.Output)) {
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
		PrClass plugin = (PrClass)event.getSource();
		LOGGER.info("Trigger send set property for " + name + " whose portId is " + plugin.getPortId() + " and value is " + value);
		wkpf.sendSetProperty(plugin.getPortId(), name, value);
	}
}