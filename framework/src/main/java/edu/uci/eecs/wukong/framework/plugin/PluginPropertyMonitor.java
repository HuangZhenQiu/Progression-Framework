package edu.uci.eecs.wukong.framework.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.manager.PluginManager;

public class PluginPropertyMonitor {
	private static Logger logger = LoggerFactory.getLogger(PluginPropertyMonitor.class);
	private PluginManager manager;
	private Map<Plugin, List<String>> monitoredProperties;
	
	private PropertyChangeListener listener = new PropertyChangeListener() {
		 public void propertyChange(PropertyChangeEvent evt) {
			 logger.info("Detected Property " + evt.getPropertyName() + "Updated.");
			 manager.updateProperty(evt);
		 }
	};
	
	public PluginPropertyMonitor(PluginManager manager) {
		this.manager = manager;
		this.monitoredProperties = new HashMap<Plugin, List<String>>();
	}
	
	public void addMonitorProperty(Plugin plugin, List<String> names) {
		List<String> properties = monitoredProperties.get(plugin);
		if (properties == null) {
			properties = new ArrayList<String>();
			monitoredProperties.put(plugin, properties);
		}

		for (String name : names) {
			if (!properties.contains(name)) {
				plugin.addPropertyChangeListener(name, listener);
				properties.add(name);
			}
		}
	}
	
	public void remove(Plugin plugin) {
		monitoredProperties.remove(plugin);
	}
}
