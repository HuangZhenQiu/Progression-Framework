package edu.uci.eecs.wukong.edge.switcher;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.AverageOperator;
import edu.uci.eecs.wukong.framework.operator.DefaultOperator;
import edu.uci.eecs.wukong.framework.operator.Operator;

public class SwitchFeatureExtensionExtension extends FeatureExtractionExtension<SwitchEdgeClass> {
	public SwitchFeatureExtensionExtension(SwitchEdgeClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public List<Operator<?>> registerOperators() {
		List<Operator<?>> operators = new ArrayList<Operator<?>>();
		
		// Set operator for plugin property switchInput
		DefaultOperator operator= new DefaultOperator();
		operator.addDataSource(1, (short)0);
		
		// Set operator for plugin property temparature
		AverageOperator avrOperator = new AverageOperator();
		avrOperator.addDataSource(2, 5);
		
		operators.add(operator);
		operators.add(avrOperator);
		return operators;
	}
}
