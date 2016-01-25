package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

/**
 * Feature abstraction is for a prclass to define how to use operator to extract feature through using operators.
 * 
 */
public abstract class FeatureExtractionExtension<T extends PipelinePrClass> extends AbstractExtension<T> {
	
	public FeatureExtractionExtension(T plugin) {
		super(plugin);
	}

	@SuppressWarnings("rawtypes")
	public abstract List<Operator> registerOperators();
}
