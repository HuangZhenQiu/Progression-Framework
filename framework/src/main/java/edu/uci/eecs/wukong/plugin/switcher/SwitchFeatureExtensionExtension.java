package edu.uci.eecs.wukong.plugin.switcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;
import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.operator.AverageOperator;
import edu.uci.eecs.wukong.framework.operator.DefaultOperator;
import edu.uci.eecs.wukong.framework.operator.Operator;

public class SwitchFeatureExtensionExtension implements FeatureAbstractionExtension {

	public List<Operator> registerOperators(Map<String, LogicalKey> logicId) {
		List<Operator> operators = new ArrayList<Operator>();
		
		// Set operator for plugin property switchInput
		DefaultOperator<Boolean> operator= new DefaultOperator<Boolean>();
		operator.addDataSource(logicId.get("switchInput"), 0);
		
		// Set operator for plugin property temparature
		AverageOperator<Double> avrOperator = new AverageOperator<Double>();
		avrOperator.addDataSource(logicId.get("temparature"), 5);
		
		operators.add(operator);
		operators.add(avrOperator);
		return operators;
	}
}
