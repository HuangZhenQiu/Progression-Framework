package edu.uci.eecs.wukong.prclass.activity;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SimoOperator;
import edu.uci.eecs.wukong.framework.test.LoadGenerator.Activity;

public class ActivityFeatureExtractionOperator extends SimoOperator<Activity> {

	protected ActivityFeatureExtractionOperator() {
		super(Activity.class);
	}

	@Override
	public List<Activity> operate(List<DataPoint<Activity>> data) {
		
		
		return null;
	}
}
