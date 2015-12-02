package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

/**
 * Feature abstraction is for a prclass to define how to use operator to extract feature through using operators.
 * 
 */
public abstract class FeatureExtractionExtension extends AbstractExtension {
	
	public FeatureExtractionExtension(PrClass plugin) {
		super(plugin);
	}

	public abstract List<Operator> registerOperators();
}