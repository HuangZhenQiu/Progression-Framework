package edu.uci.eecs.wukong.framework.extension.impl;

import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

/**
 * 
 * 
 * @author Peter
 */
public abstract class FeatureAbstractionExtension extends AbstractExtension {
	
	public FeatureAbstractionExtension(Plugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public abstract List<Operator> registerOperators(Map<String, LogicalKey> logicId);
}
