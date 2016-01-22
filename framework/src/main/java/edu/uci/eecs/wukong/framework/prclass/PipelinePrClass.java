package edu.uci.eecs.wukong.framework.prclass;

import java.beans.PropertyChangeSupport;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.reconfig.ConfigurationManager;

import com.google.common.annotations.VisibleForTesting;

/**
 * PipelinePrClass provides an interface for implementing progression capable component in progression server.
 * We provides three extensions points for data acquisition, data storage and data analysis.
 * 
 * For this kind of PrClass, progression server maintains a global buffer pool for storing raw sensor data.
 * The PipelinePrClass itself needs to define how to extract features from buffer, and processing it with 
 * its own extensions.
 *
 * @author peterhuang
 *
 */
public abstract class PipelinePrClass extends PrClass {
	private final static Logger LOGGER = LoggerFactory.getLogger(PipelinePrClass.class);
	
	@VisibleForTesting
	public PipelinePrClass(String name, boolean isTest) {
		super(name, false, false);
		this.isTest = isTest;
		if (!isTest) {
			this.support = new PropertyChangeSupport(this);
			this.configManager =  ConfigurationManager.getInstance();
		}
	}
	
	protected PipelinePrClass(String name) {
		super(name, false, false);
		this.support = new PropertyChangeSupport(this);
		this.configManager =  ConfigurationManager.getInstance();
	}

	public abstract List<Extension> registerExtension();
	
	public abstract List<String> registerContext();
	
	public AbstractProgressionExtension getProgressionExtension() {
		for(Extension extension : registerExtension()) {
			if (extension instanceof AbstractProgressionExtension) {
				return (AbstractProgressionExtension) extension;
			}
		}
		
		return null;
	}
}
