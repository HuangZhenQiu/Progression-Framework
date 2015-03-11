package edu.uci.eecs.wukong.plugin.dtdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;
import edu.uci.eecs.wukong.framework.extension.FeatureAbtractionExtension;
import edu.uci.eecs.wukong.framework.operator.AverageOperator;
import edu.uci.eecs.wukong.framework.operator.Operator;

public class DemoFeatureExtension implements FeatureAbtractionExtension<Short> {

	public List<Operator<Short>> registerOperators(List<LogicalKey> logicKeys) {
		List<Operator<Short>> operators = new ArrayList<Operator<Short>>();
		for (LogicalKey key : logicKeys) {
			AverageOperator<Short> oper = new AverageOperator<Short>();
			oper.addDataSource(key, 3/*Time Units*/);
			operators.add(oper);
		}
		
		return operators;
	}

}
