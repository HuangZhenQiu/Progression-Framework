package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;

/**
 * Feature abstraction is for a prclass to define how to use operator to extract feature through using operators.
 * 
 */
public abstract class FeatureExtractionExtension<T extends EdgePrClass> extends AbstractExtension<T> {
	public static final String FEATURE_EXTRACTION_PREFIX = "feature-extraction";
	
	public FeatureExtractionExtension(T plugin) {
		super(plugin);
	}

	public abstract List<Operator<?>> registerOperators();
	
	@Override
	public String prefix() {
		return FEATURE_EXTRACTION_PREFIX;
	}
}
