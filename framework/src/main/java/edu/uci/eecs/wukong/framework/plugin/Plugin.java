package edu.uci.eecs.wukong.framework.plugin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import edu.uci.eecs.wukong.framework.extension.Extension;

public abstract class Plugin {
	private String name;
	private String appId;
	private boolean online;
	private PropertyChangeSupport support;
	
	public Plugin(String appId, String name, boolean online) {
		this.appId = appId;
		this.name = name;
		this.online = online;
	}

	public Plugin(String appId, String name) {
		this.appId = appId;
		this.online = false;
		this.support = new PropertyChangeSupport(this);
	}

	public abstract List<Extension> registerExtension();
	
	public abstract List<String> registerContext();
	
	protected final void addPropertyChangeListener(
			String propertyName, PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
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
}
