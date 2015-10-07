package edu.uci.eecs.wukong.framework.extension;

import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

/**
 * 
 * 
 * @author Peter
 */
public abstract class FeatureAbstractionExtension extends AbstractExtension {
	
	public FeatureAbstractionExtension(PrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public abstract List<Operator> registerOperators();
}
