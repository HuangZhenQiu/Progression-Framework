package edu.uci.eecs.wukong.plugin.switcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.context.BaseContext;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.context.UserContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public class SwitchLearningExtension extends LearningExtension<Number> {
	
	private Object content;
	private Map<String, Double> model;
	
	public SwitchLearningExtension(Plugin plugin) {
		super(plugin);
		model = new HashMap<String, Double>();
	}

	public void apply(List<Number> data, ExecutionContext context) {
		BaseContext userContext = context.getContext("UID");
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
