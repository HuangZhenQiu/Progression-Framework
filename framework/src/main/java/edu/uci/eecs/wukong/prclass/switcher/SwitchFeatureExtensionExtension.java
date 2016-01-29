package edu.uci.eecs.wukong.prclass.switcher;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.AverageOperator;
import edu.uci.eecs.wukong.framework.operator.DefaultOperator;
import edu.uci.eecs.wukong.framework.operator.Operator;

public class SwitchFeatureExtensionExtension extends FeatureExtractionExtension<SwitchPrClass> {
	public SwitchFeatureExtensionExtension(SwitchPrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public List<Operator<?>> registerOperators() {
		List<Operator<?>> operators = new ArrayList<Operator<?>>();
		
		// Set operator for plugin property switchInput
		DefaultOperator<Integer> operator= new DefaultOperator<Integer>();
		operator.addDataSource(1, 0);
		
		// Set operator for plugin property temparature
		AverageOperator<Double> avrOperator = new AverageOperator<Double>();
		avrOperator.addDataSource(2, 5);
		
		operators.add(operator);
		operators.add(avrOperator);
		return operators;
	}
}
