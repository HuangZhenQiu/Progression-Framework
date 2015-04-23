package edu.uci.eecs.wukong.framework.plugin;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import edu.uci.eecs.wukong.framework.extension.Extension;

public abstract class Plugin {
	private String name;
	private PropertyChangeSupport support;
	public Plugin(String name) {
		this.name = name;
		this.support = new PropertyChangeSupport(this);
	}

	public abstract List<Extension> registerExtension();
	
	public abstract List<String> registerContext();
	
	protected final void addPropertyChangeListener(
			String propertyName, PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}
	
	public String getName() {
		return name;
	}
}
