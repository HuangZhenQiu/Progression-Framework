package edu.uci.eecs.wukong.framework.prclass;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrClassPropertyMonitor {
	private static Logger logger = LoggerFactory.getLogger(PrClassPropertyMonitor.class);
	private PrClassManager manager;
	private Map<PipelinePrClass, List<String>> monitoredProperties;
	
	private PropertyChangeListener listener = new PropertyChangeListener() {
		 public void propertyChange(PropertyChangeEvent evt) {
			 logger.info("Detected Property " + evt.getPropertyName() + " Updated.");
			 manager.updateProperty(evt);
		 }
	};
	
	public PrClassPropertyMonitor(PrClassManager manager) {
		this.manager = manager;
		this.monitoredProperties = new HashMap<PipelinePrClass, List<String>>();
	}
	
	public void addMonitorProperty(PipelinePrClass plugin, List<String> names) {
		List<String> properties = monitoredProperties.get(plugin);
		if (properties == null) {
			properties = new ArrayList<String>();
			monitoredProperties.put(plugin, properties);
		}

		for (String name : names) {
			if (!properties.contains(name)) {
				plugin.addPropertyChangeListener(name, listener);
				properties.add(name); 
				logger.info("Add monitored output property " + name + " for PrClass " + plugin);
			}
		}
	}
	
	public void remove(PipelinePrClass plugin) {
		monitoredProperties.remove(plugin);
	}
}
