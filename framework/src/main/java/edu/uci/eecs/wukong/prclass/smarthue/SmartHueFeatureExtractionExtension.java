package edu.uci.eecs.wukong.prclass.smarthue;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.operator.AverageOperator;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class SmartHueFeatureExtractionExtension extends
		FeatureExtractionExtension {

	public SmartHueFeatureExtractionExtension(PipelinePrClass plugin) {
		super(plugin);
	}

	@Override
	public List<Operator> registerOperators() {
		List<Operator> operators = new ArrayList<Operator>();
		AverageOperator indoorAvr = new AverageOperator();
		indoorAvr.addDataSource(1 /* property 1 */, 5 /* seconds */);
		AverageOperator outdoorAvr = new AverageOperator();
		outdoorAvr.addDataSource(2, 5);
		operators.add(indoorAvr);
		operators.add(outdoorAvr);
		return operators;
	}

}
