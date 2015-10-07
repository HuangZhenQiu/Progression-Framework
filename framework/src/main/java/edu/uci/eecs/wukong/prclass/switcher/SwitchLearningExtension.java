package edu.uci.eecs.wukong.prclass.switcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.UserFactor;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class SwitchLearningExtension extends LearningExtension<Number> {
	
	private Object content;
	private Map<String, Double> model;
	
	public SwitchLearningExtension(PrClass plugin) {
		super(plugin);
		model = new HashMap<String, Double>();
	}

	public void apply(List<Number> data, ExecutionContext context) {
		BaseFactor userContext = context.getContext("UID");
		if (userContext instanceof UserFactor) {
			String uid = ((UserFactor) userContext).getUid();
			
			// We know data contains a boolean for switch and a double temperature.
			// Insert data into content;
		}
	}

	public Object train() throws Exception {
		// Learn model from content
		return model;
	}
}
