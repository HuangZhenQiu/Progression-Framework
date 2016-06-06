package edu.uci.eecs.wukong.edge.activity;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;

public class ActivityDiscoveryExtension extends LearningExtension<Double, ActivityRecgonitionPrClass> {

	public ActivityDiscoveryExtension(ActivityRecgonitionPrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void apply(List<Double> data, ExecutionContext context) {
		
		
	}

	@Override
	public Object train() throws Exception {
		return null;
	}
}
