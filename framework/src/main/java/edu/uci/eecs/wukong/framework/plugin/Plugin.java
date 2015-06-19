package edu.uci.eecs.wukong.framework.plugin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import edu.uci.eecs.wukong.framework.extension.Extension;

public abstract class Plugin {
	private static int id = 0;
	private int pluginId;
	private String name;
	private String appId;
	private boolean online;
	private boolean learning;
	private PropertyChangeSupport support;
	
	public Plugin(String appId, String name, boolean online) {
		this.appId = appId;
		this.name = name;
		this.online = online;
		this.pluginId = id ++;
	}

	public Plugin(String appId, String name) {
		this.appId = appId;
		this.online = false;
		this.learning = false;
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

	public String getAppId() {
		return this.appId;
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

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public int getPluginId() {
		return pluginId;
	}

	public void setPluginId(int pluginId) {
		this.pluginId = pluginId;
	}
}
