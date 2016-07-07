package edu.uci.eecs.wukong.edge.activity;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;

public class ActivityFeatureExtractionExtension extends FeatureExtractionExtension<ActivityRecgonitionEdgeClass> {

	public ActivityFeatureExtractionExtension(ActivityRecgonitionEdgeClass plugin) {
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
