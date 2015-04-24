package edu.uci.eecs.wukong.plugin.switcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.context.UserContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;

public class SwitchLearningExtension implements LearningExtension<Number> {
	
	private Object content;
	private Map<String, Double> model;
	
	public SwitchLearningExtension() {
		model = new HashMap<String, Double>();
	}

	public void apply(List<Number> data, ExecutionContext context) {
		Context userContext = context.getContext("UID");
		if (userContext instanceof UserContext) {
			String uid = ((UserContext) userContext).getUid();
			
			// We know data contains a boolean for switch and a double temperature.
			// Insert data into content;
		}
	}

	public Object train() throws Exception {
		// Learn model from content
		return model;
	}
}
