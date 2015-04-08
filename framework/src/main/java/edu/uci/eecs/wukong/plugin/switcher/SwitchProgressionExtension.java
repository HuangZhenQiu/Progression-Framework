package edu.uci.eecs.wukong.plugin.switcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.context.UserContext;
import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.entity.ConfigurationEntity;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager.ConfigurationType;

public class SwitchProgressionExtension implements ProgressionExtension {
	private static String COMPONENT_ID = "6";
	// User ID to threshold Map
	private Map<String, Integer> userThresholdMap;
	
	public SwitchProgressionExtension(){
		this.userThresholdMap = new HashMap<String, Integer>();
		//Add predefined rules map here.
	}
	
	public void activate(Object model) {
		// TODO Auto-generated method stub
		
	}

	public List<ConfigurationCommand> execute(List data, ExecutionContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ConfigurationCommand> execute(Context context) {
		if (context instanceof UserContext) {
			UserContext userContext = (UserContext)context;
			Integer threshold = userThresholdMap.get(userContext.getUid());
			if(threshold != null) {	
				List<ConfigurationCommand> commands = new ArrayList<ConfigurationCommand>();
				ConfigurationCommand command = new ConfigurationCommand("Master", ConfigurationType.POST);
				command.addEntity(new ConfigurationEntity(COMPONENT_ID, threshold));
				commands.add(command);
				return commands;
			}
		}
		
		return ConfigurationCommand.getEmptyCommand();
	}

	public List<ConfigurationCommand> execute() {
		// TODO Auto-generated method stub
		return null;
	}

}
