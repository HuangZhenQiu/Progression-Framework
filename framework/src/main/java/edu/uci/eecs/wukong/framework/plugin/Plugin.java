package edu.uci.eecs.wukong.framework.plugin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;

/**
 * Since we assume there is only one application running at any time,
 * appId which is unique for a FBP is not used right now. 
 * 
 * In current implementation, all of the PrClass is installed in progression
 * server in advance. To easily integrate the mapping and deploy in master,
 * The strategy of plugin initialize is like this:
 * 
 * 1 Load Plugin Java Classes in Progression Server
 * 2 Create an instance for each plugin on particular port
 * 3 WuObjects (for Plugin instance) can be used for mapping and deployment
 * 4 During Deployment, fetch the link table and component map
 * 5 Use link table and component map to bind the plugin instance to pipeline 
 * 
 * In this way, we split the initialization of a plugin into two stages. First
 * for object initialization for mapping, and second stage for data binding
 * and extension registration.
 *   
 * TODO (Peter Huang) Make plugin instance reusable for multiple FBP
 *
 */
public abstract class Plugin {
	/* Port starts from 1*/
	private static byte id = 1; 
	private byte portId;
	private String name;
	private boolean online;
	private boolean learning;
	protected PropertyChangeSupport support;
	
	public Plugin(String name, boolean online) {
		this.name = name;
		this.online = online;
		this.portId = id ++;
	}

	public Plugin(String name) {
		this.online = false;
		this.learning = false;
		this.portId = id ++;
		this.support = new PropertyChangeSupport(this);
	}

	public abstract List<Extension> registerExtension();
	
	public abstract List<String> registerContext();
	
	protected final void addPropertyChangeListener(
			String propertyName, PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}
	
	public boolean isLearning() {
		return learning;
	}

	public void setLearning(boolean learning) {
		this.learning = learning;
	}
	
	public String getName() {
		return this.name;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public PropertyChangeSupport getSupport() {
		return support;
	}

	public void setSupport(PropertyChangeSupport support) {
		this.support = support;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getPortId() {
		return portId;
	}
	
    @Override
	public String toString() {
    	return "Plugin[name = " + name + ", portId = " + portId + "]";
    }
}
