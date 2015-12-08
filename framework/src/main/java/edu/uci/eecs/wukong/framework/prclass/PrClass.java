package edu.uci.eecs.wukong.framework.prclass;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.annotation.Annotation;
import java.util.List;
import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import com.google.common.annotations.VisibleForTesting;
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
public abstract class PrClass {
	private final static Logger LOGGER = LoggerFactory.getLogger(PrClass.class);
	/* Port starts from 1*/
	private static byte id = 1; 
	private byte portId;
	private String name;
	private boolean online;
	private boolean learning;
	protected PropertyChangeSupport support;
	private ConfigurationManager configManager;
	
	@VisibleForTesting
	public PrClass(String name, boolean isTest) {
		this(name, false, false);
		if (!isTest) {
			this.support = new PropertyChangeSupport(this);
			this.configManager =  ConfigurationManager.getInstance();
		}
	}
	
	public PrClass(String name) {
		this(name, false, false);
		this.support = new PropertyChangeSupport(this);
		this.configManager =  ConfigurationManager.getInstance();
	}
	
	private PrClass(String name, boolean online, boolean learning) {
		this.name = name;
		this.online = online;
		this.portId = id ++;
		this.online = online;
		this.learning = learning;

	}

	public abstract List<Extension> registerExtension();
	
	public abstract List<String> registerContext();
	
	public ConfigurationManager getConfigurationManager() {
		return configManager;
	}
	
	public boolean isInitialized() {
		Annotation[] annotations = PrClass.class.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(WuProperty.class)) {
				WuProperty wuProperty = (WuProperty) annotation;
				try {
					Field field = this.getClass().getDeclaredField(wuProperty.name());
					if (field.get(this) == null) {
						return false;
					}
				} catch (Exception e) {
					LOGGER.error("Can't find field " + wuProperty.name() + " for PrClass " + this.name);
				}
			}
		}
		
		return true;
	}
	
	public void publish(String topic, BaseFactor value) {
		configManager.publish(topic, value);
	}
	
	public void remap() throws UnsupportedOperationException {
		if (this instanceof SystemPrClass) {
			configManager.remapping("");
		} else {
			throw new UnsupportedOperationException("Remap can't be used by PrClass directly");
		}
	}
	
	public final void addPropertyChangeListener(
			String propertyName, PropertyChangeListener listener) {
		support.addPropertyChangeListener(propertyName, listener);
	}
	
	public AbstractProgressionExtension getProgressionExtension() {
		for(Extension extension : registerExtension()) {
			if (extension instanceof AbstractProgressionExtension) {
				return (AbstractProgressionExtension) extension;
			}
		}
		
		return null;
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
    	return "PrClass[name = " + name + ", portId = " + portId + "]";
    }
}
