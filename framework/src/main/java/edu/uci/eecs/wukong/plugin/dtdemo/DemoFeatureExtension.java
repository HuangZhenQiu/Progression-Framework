package edu.uci.eecs.wukong.plugin.dtdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;
import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.operator.AverageOperator;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public class DemoFeatureExtension extends FeatureAbstractionExtension {

	
	public DemoFeatureExtension(Plugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public List<Operator> registerOperators() {
		List<Operator> operators = new ArrayList<Operator>();
		return operators;
	}

}
