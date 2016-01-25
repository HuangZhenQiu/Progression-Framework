package edu.uci.eecs.wukong.framework.prclass;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;

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
		super(name);
	}
	
	protected PipelinePrClass(String name) {
		super(name, false, false, PrClass.PrClassType.PIPELINE_PRCLASS);
		
	}
	
	protected PipelinePrClass(String name, PrClass.PrClassType type) {
		super(name, false, false, type);
	}

	public abstract List<Extension> registerExtension();
	
	public abstract List<String> registerContext();
	
	public AbstractProgressionExtension<?> getProgressionExtension() {
		for(Extension extension : registerExtension()) {
			if (extension instanceof AbstractProgressionExtension) {
				return (AbstractProgressionExtension<?>) extension;
			}
		}
		
		return null;
	}
}
