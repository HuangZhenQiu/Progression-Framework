package edu.uci.eecs.wukong.plugin.switcher;

import java.util.HashMap;
import java.util.Map;

import edu.uci.eecs.wukong.framework.api.ContextExecutable;
import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.UserContext;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public class SwitchProgressionExtension extends AbstractProgressionExtension
	implements ContextExecutable {
	// User ID to threshold Map
	private Map<String, Double> userThresholdMap;
	
	public SwitchProgressionExtension(Plugin plugin){
		super(plugin);
		this.userThresholdMap = new HashMap<String, Double>();
		//Add predefined rules map here.
	}
	
	public void activate(Object model) {
		userThresholdMap.putAll((Map<String, Double>) model);
	}
	
	public void execute(Context context) {
		if (context instanceof UserContext) {
			UserContext userContext = (UserContext)context;
			Double threshold = userThresholdMap.get(userContext.getUid());
			if(threshold != null) {	
				SwitchPlugin plugin =  (SwitchPlugin)this.getPlugin();
				plugin.setThreshold(threshold);
			}
		}
	}
}
