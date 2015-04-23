package edu.uci.eecs.wukong.framework.plugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import edu.uci.eecs.wukong.framework.manager.PluginManager;

public class PluginPropertyMonitor {
	private PluginManager manager;
	private PropertyChangeListener listener = new PropertyChangeListener() {
		 public void propertyChange(PropertyChangeEvent evt) {
			 manager.updateProperty(evt);
		 }
	};
	
	public PluginPropertyMonitor(PluginManager manager) {
		this.manager = manager;
	}
	
	public void addMonitorProperty(Plugin plugin, List<String> names) {
		for (String name : names) {
			plugin.addPropertyChangeListener(name, listener);
		}
	}
}
