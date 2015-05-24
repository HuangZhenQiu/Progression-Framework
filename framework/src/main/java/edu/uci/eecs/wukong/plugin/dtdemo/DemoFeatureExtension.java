package edu.uci.eecs.wukong.plugin.dtdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;
import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.operator.AverageOperator;
import edu.uci.eecs.wukong.framework.operator.Operator;

public class DemoFeatureExtension implements FeatureAbstractionExtension {

	public List<Operator> registerOperators(Map<String, LogicalKey> logicKeys) {
		List<Operator> operators = new ArrayList<Operator>();
		return operators;
	}

}
