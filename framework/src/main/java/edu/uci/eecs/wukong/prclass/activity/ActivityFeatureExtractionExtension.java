package edu.uci.eecs.wukong.prclass.activity;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;

public class ActivityFeatureExtractionExtension extends FeatureExtractionExtension<ActivityRecgonitionPrClass> {

	public ActivityFeatureExtractionExtension(ActivityRecgonitionPrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Operator<?>> registerOperators() {
		List<Operator<?>> operators = new ArrayList<Operator<?>>();
		operators.add(new ActivityFeatureExtractionOperator());
		return operators;
	}
}
