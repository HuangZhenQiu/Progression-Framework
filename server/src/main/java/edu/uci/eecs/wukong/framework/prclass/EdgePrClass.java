package edu.uci.eecs.wukong.framework.prclass;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;

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
public abstract class EdgePrClass extends PrClass {
	private final static Logger LOGGER = LoggerFactory.getLogger(EdgePrClass.class);
	
	@VisibleForTesting
	public EdgePrClass(String name, boolean isTest) {
		super(name);
	}
	
	protected EdgePrClass(String name, PrClassMetrics metrics) {
		super(name, false, false, PrClass.PrClassType.PIPELINE_PRCLASS, metrics);
		
	}
	
	protected EdgePrClass(String name, PrClass.PrClassType type, PrClassMetrics metrics) {
		super(name, false, false, type, metrics);
	}

	public abstract List<Extension> registerExtension();
	
	public abstract List<String> registerContext();
	
	public AbstractExecutionExtension<?> getProgressionExtension() {
		for(Extension extension : registerExtension()) {
			if (extension instanceof AbstractExecutionExtension) {
				return (AbstractExecutionExtension<?>) extension;
			}
		}
		
		return null;
	}
}
