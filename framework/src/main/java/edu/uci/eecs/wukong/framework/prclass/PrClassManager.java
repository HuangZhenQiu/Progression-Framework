package edu.uci.eecs.wukong.framework.prclass;

import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.exception.PluginNotFoundException;
import edu.uci.eecs.wukong.framework.factor.SceneManager;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.prclass.PrClass.PrClassType;
import edu.uci.eecs.wukong.framework.property.Activity;
import edu.uci.eecs.wukong.framework.property.Location;
import edu.uci.eecs.wukong.framework.property.Response;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.model.WuClassModel;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.StateModel;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.PipelineUtil;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.framework.state.StateUpdateListener;

import java.beans.PropertyChangeEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.Exception;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrClassManager implements PrClassInitListener {
	private final static Logger LOGGER = LoggerFactory.getLogger(PrClassManager.class);
	private static final String PLUGIN_DEFINATION_PATH = "plugins.txt";
	// private static final String PLUGIN_PATH = "edu.uci.eecs.wukong.prclass";
	private static final Configuration configuration = Configuration.getInstance();
	private BufferManager bufferManager;
	private SceneManager contextManager;
	private PrClassPropertyMonitor propertyMonitor;
	private Pipeline pipeline;
	private PrClassMetrics prClassMetrics;
	private Timer timer;
	private Map<PrClass, SimpleTimerTask> prClassTimerMap;
	private List<PrClass> plugins;
	/* WuclassId to WuClass model */
	private Map<Short, WuClassModel> registedClasses;
	/* Binded WuObjects */
	private List<WuObjectModel> bindedWuObjects;
	private WKPF wkpf;
	/* Name to number of objects*/
	private Map<String, Integer> pluginsMap;
	private List<StateUpdateListener> listeners;
	
	public PrClassManager(WKPF wkpf, SceneManager contextManager,
			Pipeline pipeline, BufferManager bufferManager, PrClassMetrics prClassMetrics) {
		this.bufferManager = bufferManager;
		this.contextManager = contextManager;
		this.pipeline = pipeline;
		this.prClassMetrics = prClassMetrics;
		this.timer = new Timer();
		this.propertyMonitor = new PrClassPropertyMonitor(this);
		this.prClassTimerMap = new HashMap<PrClass, SimpleTimerTask>();
		this.registedClasses = new HashMap<Short, WuClassModel>();
		this.plugins = new ArrayList<PrClass>();
		this.bindedWuObjects = new ArrayList<WuObjectModel>();
		this.wkpf = wkpf;
		this.pluginsMap =  new HashMap<String, Integer> ();
		this.listeners = new ArrayList<StateUpdateListener> ();
		this.loadPrClassDefinition();
	}
	
	private class SimpleTimerTask extends TimerTask {
		private SimplePrClass executable;

		public SimpleTimerTask(SimplePrClass executable) {
			this.executable = executable;
		}
		
		@Override
		public void run() {
			executable.update();
		}
	}

	public void register(StateUpdateListener listener) {
		this.listeners.add(listener);
	}
	
	private void fireUpdateEvent() {
		for (StateUpdateListener listener : listeners) {
			listener.update();
		}
 	}
	
	private void loadPrClassDefinition() {
		try {
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PLUGIN_DEFINATION_PATH);
			InputStreamReader streamReader = new InputStreamReader(inputStream);
			BufferedReader reader = new BufferedReader(streamReader);
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line != "") {
					StringTokenizer tokenizer = new StringTokenizer(line);
					if (tokenizer.countTokens() == 2) {
						try {
							String pluginName = tokenizer.nextToken();
							Integer number = Integer.parseInt(tokenizer.nextToken());
							pluginsMap.put(pluginName, number);
						} catch (Exception e) {
							LOGGER.info("Failt to parse the second part of plugin config : " + line);
						}
					} else {
						LOGGER.info("Unrecgonized plugin string format : " + line);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Plugin Definition File '" + PLUGIN_DEFINATION_PATH + "' not found in the classpath");
			System.exit(-1);
		}
	}
	
	/**
	 * Init the Wuclasses that are able to discovered through WKPF. Currently, 
	 * we create an instance for each Wuclass for exposing Wuobject to mapping
	 * in master. But these WuObject's extensions will not be bind to pipeline
	 * until receiving reprogramming commands from master.
	 * 
	 * @throws Exception
	 */
	public void init(StateModel model) throws Exception {
		for (Entry<String, Integer> pluginEntry : pluginsMap.entrySet()) {
			ClassLoader loader = PrClassManager.class.getClassLoader();
			Class<?> c = loader.loadClass(pluginEntry.getKey());
			WuClassModel wuClassModel = createWuClassModel(pluginEntry.getKey(), c);
			
			if (wuClassModel != null) {
				LOGGER.info("Initialized Wuclass in progression server : " + wuClassModel);
				for (int i = 0; i< pluginEntry.getValue(); i++) {
					// A temporary solution for easier mapping and deployment.
					// Create an instance for each plugin classes as hard WuClass.
					Constructor<?> constructor;
					PrClass plugin;
					if (wuClassModel.getType().equals(PrClassType.SYSTEM_PRCLASS)) {
						constructor = c.getConstructor(WKPF.class, PrClassMetrics.class);
						plugin = (PrClass) constructor.newInstance(wkpf, prClassMetrics);
					} else {
						constructor = c.getConstructor(PrClassMetrics.class);
						plugin = (PrClass) constructor.newInstance(prClassMetrics);
					}

					prClassMetrics.addPrClassMeter(plugin);
					plugins.add(plugin);
					WuObjectModel wuObjectModel = new WuObjectModel(wuClassModel, plugin);
					wkpf.addWuObject(plugin.getPortId(), wuObjectModel);
					LOGGER.info("Registered " + plugin.getName() +  " in progression server on port " + plugin.getPortId());
				}
			}
		}
		
		// Recovery from failure status
		if (model != null && model.getBindedWuObject() != null && model.getBindedWuObject().size() > 0) {
			bindPlugins(model.getBindedWuObject());
		}
	}
	
	private WuClassModel createWuClassModel(String path, Class<?> c) throws ClassNotFoundException {
		WuClass wuclass = c.getAnnotation(WuClass.class);
		if (wuclass == null) {
			LOGGER.error("Can't find WuClass annotation for PrClass " + path);
			return null;
		}
		
		if (PipelinePrClass.class.isAssignableFrom(c)
				|| SimplePrClass.class.isAssignableFrom(c)
				|| SystemPrClass.class.isAssignableFrom(c)) {
			PrClass.PrClassType type = null;
			if (SimplePrClass.class.isAssignableFrom(c)) {
				type = PrClass.PrClassType.SIMPLE_PRCLASS;
			} else if (SystemPrClass.class.isAssignableFrom(c)) {
				type = PrClass.PrClassType.SYSTEM_PRCLASS;
			} else if(PipelinePrClass.class.isAssignableFrom(c)) {
				type = PrClass.PrClassType.PIPELINE_PRCLASS;
			}
			
			WuClassModel wuClassModel =  new WuClassModel(wuclass.id(), type);
			for (Field field : c.getDeclaredFields()) {
				String name = field.getName();
				Annotation[] annotations = field.getDeclaredAnnotations();
				for (Annotation annotation : annotations) {
					if (annotation.annotationType().equals(WuProperty.class)) {
						WuProperty property = (WuProperty)annotation;
						wuClassModel.addProperty(name, property, field.getType());
					}
				}
			}
			
			registedClasses.put(wuclass.id(), wuClassModel);
			wkpf.addWuClass(wuClassModel);
			
			return wuClassModel;
		}
		
		
		LOGGER.error("Try to register an unsupport PrClass type " + c.getCanonicalName());
		return null;
	}
	
	/**
	 * Bind wuobjects used in a FBP with meta data sent by remote programming. It can also be used by
	 * state manager to recovery the state of plugin manager
	 * 
	 * @param wuobjectMap map port to wuclassId
	 */
	public void bindPlugins(List<WuObjectModel> objects) {
		LOGGER.info("Start to bind plugins into plugin manager, the size of objects is " + objects.size());
		bindedWuObjects.clear();
		for (WuObjectModel model : objects) {
			if (model.getPrClass().isInitialized()) {
				bindPlugin(model);
				bindedWuObjects.add(model);
			} else {
				LOGGER.error("Fail to bind WuObject with class type " + model.getPrClass().getName() + " because of missing init values");
			}
		}
		
		// Update wuobject information
		fireUpdateEvent();
	}
	
	/**
	 * When master do the remote programming, we need to deallocate binds from physical key to buffer
	 * for plugins, and also unregister the extensions in pipeline. 
	 */
	public void unbindPlugins() {
    	for (WuObjectModel model  : bindedWuObjects) {
    		if (model.getType().getType().equals(PrClass.PrClassType.PIPELINE_PRCLASS)) {
    			PipelinePrClass pipePrClass = (PipelinePrClass) model.getPrClass();
    			contextManager.unsubscribe(pipePrClass);
    			bufferManager.unbind(model);
    			pipeline.unregisterExtension(model);
    			LOGGER.info("Finished bind pipeline prclass with context manager, pipeline and property monitor.");
    		} else if (model.getType().getType().equals(PrClass.PrClassType.SIMPLE_PRCLASS)) {
    			bufferManager.unbind(model);
    			unbindSimplePrClassTimer((SimplePrClass)model.getPrClass());
    		} else if (model.getType().getType().equals(PrClass.PrClassType.SYSTEM_PRCLASS)) {
    			pipeline.unregisterExtension(model);
    		}
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
		bindPropertyUpdateEvent(prClass);
		
		if (model.getType().getType().equals(PrClass.PrClassType.PIPELINE_PRCLASS)) {
			PipelinePrClass pipePrClass = (PipelinePrClass) prClass;
			contextManager.subscribe(pipePrClass, pipePrClass.registerContext());
			bufferManager.bind(model);
			pipeline.registerExtension(model);
			LOGGER.info("Finished bind pipeline prclass with context manager, pipeline and property monitor.");
		} else if (model.getType().getType().equals(PrClass.PrClassType.SIMPLE_PRCLASS)) {
			// bind the input property to channels
			bufferManager.bind(model);
			bindSimplePrClassTimer((SimplePrClass)model.getPrClass());
		} else if (model.getType().getType().equals(PrClass.PrClassType.SYSTEM_PRCLASS)) {
			pipeline.registerExtension(model);
			LOGGER.info("Finished bind system prclass.");
		}
	}
	
	private void bindSimplePrClassTimer(SimplePrClass prclass) {
		// start timer 
		float internal = PipelineUtil.getIntervalFromClassAnnotation(prclass);
		SimpleTimerTask timerTask = new SimpleTimerTask(prclass);
		timer.scheduleAtFixedRate(timerTask, 0, new Float(internal * configuration.getExtensionTimerUnit()).longValue());
		prClassTimerMap.put(prclass, timerTask);
		LOGGER.info("Registered Timer Executor for every " + internal + "seconds  for simple prclass "
				+ prclass.getName() + " of port " + prclass.getPortId());
	}
	
	private void unbindSimplePrClassTimer(SimplePrClass prclass) {
		SimpleTimerTask task = prClassTimerMap.get(prclass);
		task.cancel();
		prClassTimerMap.remove(prclass);
		LOGGER.info("unRegistered Timer Executor  for simple prclass "
				+ prclass.getName() + " of port " + prclass.getPortId());
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
		PipelinePrClass plugin = (PipelinePrClass)event.getSource();
		int length = 0;
		if (value instanceof Byte || value instanceof Boolean) {
			length = 1;
		} else if (value instanceof Short) {
			length = 2;
		} else if (value instanceof Integer) {
			length = 2;
		} else if (value instanceof Location) {
			length = 12 + 4 /* sequence int*/;
		} else if (value instanceof Activity) {
			length = 14 + 4 /* sequence int */;
		} else if (value instanceof Response) {
			length = 4;
		} else  {
			LOGGER.info("Stop to update unrecgonized proeprty type: " + Object.class);
			return;
		}
		
		LOGGER.info("Trigger send set property for " + name + " whose portId is " + plugin.getPortId() + " and value is " + value);
		wkpf.sendSetProperty(plugin.getPortId(), name, value, length);
	}
	
	
	public List<WuObjectModel> getBindedWuObjects() {
		return this.bindedWuObjects;
	}
}