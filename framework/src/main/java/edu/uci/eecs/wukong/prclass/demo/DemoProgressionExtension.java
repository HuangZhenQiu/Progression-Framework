/**
 * This extension implements the rules that designed by Professor Lin. In the demo, we will
 * use four slides to control four groups of lights. Group 1 includes 3, 4, 5. Group 2
 * includes 7, 7, 8, 8. Group 3 includes 12. Group four includes 15. If there is no people
 * in the detectable range of kinnect, then all the light should be closed. In the demo, every 
 * light's lightness can be adjusted between 0 - 99.
 * 
 * 1) When some one temporarily enter into the detectable section (3, 4, 6), the extension should change lights
 * in group 1 to 50.
 * 2) If user left the room, we just turn off lights of group one after two seconds.
 * 3）When some one stay in kichen (4 + 6) for 5 seconds, we turn all lights in Group 1 and Group 2
 * to 99.
 * 4) If user left after finishing works in kichen, it firstly turn off lights in Group 1, and then Group
 * 2 after 2 seconds.
 * 5) If user enter section 5 and stay there for 5 seconds, it turn group 1 to 50, and Group 3 and 4 
 * to 99
 * 6) If user left section 5, it turn off Group 4, 3 and 1 sequentially.
 * 
 */
package edu.uci.eecs.wukong.prclass.demo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.entity.ConfigurationEntity;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.entity.HueEntity;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager.ConfigurationType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class DemoProgressionExtension extends ProgressionExtension<FeatureEntity> {
	private static Logger logger = LoggerFactory.getLogger(DemoProgressionExtension.class);
	private static Configuration configuration = Configuration.getInstance();
	private static String KICHEN_SLIDER_COMPONENT_ID = configuration.getKichenSliderId(); // 3, 4, 5
	private static String TABLE_SLIDER_COMPONENT_ID = configuration.getTableSliderId(); // 7, 7, 8, 8
	private static String OUTER_SLIDER_COMPONENT_ID = configuration.getOuterSliderId(); // 12
	private static String WALL_LIGHT_SLIDER_COMPONENT_ID = configuration.getWallSliderId(); // 15
	private static int LEVEL_ONE = 0;
	private static int LEVEL_TWO = 50;
	private static int LEVEL_THREE = 99;
	private static int status; // 0 Nobody, 1 kitchen PassOver, 2, Passover left, 3 kitchen Stay, 4 kitchen left after stay, 5 table stay, 6 stable left after stay 
	private DemoFactor lastContext = null;
	private static long lastTime = 0;
	
	public DemoProgressionExtension(PrClass plugin) {
		super(plugin);
	}

	// Triggered by general data pipeline
	public  List<ConfigurationCommand> execute(List<FeatureEntity> data, ExecutionContext context) {
		return ConfigurationCommand.getEmptyCommand();
	}
	
	// Triggered by context switch
	public  List<ConfigurationCommand> execute(BaseFactor context) {
		logger.info("DemoProgressionExtension received new context");
		List<ConfigurationCommand> commands = new ArrayList<ConfigurationCommand>();
		List<ConfigurationEntity> entities = new ArrayList<ConfigurationEntity>();
		if(context instanceof DemoFactor) {
			DemoFactor demoContext = (DemoFactor) context;
			logger.info(">>>>>>>>>>>>status:"+status);
			if (status == 0) { //Nobody
				if (isPeopleExist(demoContext)) {
					status = 1;
					logger.info("<<<<<<<status to:" + status);
					lastTime = demoContext.getTimestamp();
					commands.add(generateEnterRoomCommand(entities));
				} else {
					if (isLastEnoughTime(lastTime, demoContext, configuration.getDemoKichenSeconds())) {
						lastTime = demoContext.getTimestamp();
						commands.add(generateCloseHueCommand());
						commands.add(generateTurnOffCommand(entities));
					}
				}
			} else if (status == 1) {
				if (isInKichen(demoContext)) {
					if (isLastEnoughTime(lastTime, demoContext, configuration.getDemoKichenSeconds())) {
						status = 3;
						logger.info("<<<<<<<status to:" + status);
						lastTime = demoContext.getTimestamp();
						commands.add(generateInKichenCommand(entities));
					}
				} else if (isInTableConversation(demoContext)) {
					status = 5;
					logger.info("<<<<<<<status to:" + status);
					lastTime = demoContext.getTimestamp();
					commands.add(generateInTableConversation(entities));
				} else {
					if (isLastEnoughTime(lastTime, demoContext, configuration.getDemoKichenSeconds())) {
						status = 0;
						logger.info("<<<<<<<status to:" + status);
						lastTime = demoContext.getTimestamp();
						commands.add(generateCloseHueCommand());
						commands.add(generateTurnOffCommand(entities));
					}
				}
			} else if (status == 3) {
				if (isInKichen(demoContext)) {
					if (isLastEnoughTime(lastTime, demoContext, configuration.getDemoKichenSeconds())) {
						lastTime = demoContext.getTimestamp();
						commands.add(generateInKichenCommand(entities));
					}
				} else if (isInTableConversation(demoContext)) {
					status = 5;
					logger.info("<<<<<<<status to:" + status);
					lastTime = demoContext.getTimestamp();
					commands.add(generateInTableConversation(entities));
					commands.add(generateOpenHueCommand());
				} else {
					if (isLastEnoughTime(lastTime, demoContext, configuration.getDemoKichenSeconds())) {
						status = 1;
						logger.info("<<<<<<<status to:" + status);
						lastTime = demoContext.getTimestamp();
						commands.add(generateEnterRoomCommand(entities));
					}
				}
			} else if (status == 5) {
				if (isInKichen(demoContext)) {
					if (isLastEnoughTime(lastTime, demoContext, configuration.getDemoKichenSeconds())) {
						status = 3;
						logger.info("<<<<<<<status to:" + status);
						lastTime = demoContext.getTimestamp();
						commands.add(generateInKichenCommand(entities));
					}
				} else if (isInTableConversation(demoContext)) {
					if (isLastEnoughTime(lastTime, demoContext, configuration.getDemoKichenSeconds())) {
						lastTime = demoContext.getTimestamp();
						commands.add(generateInTableConversation(entities));
						commands.add(generateOpenHueCommand());
					}
				} else {
					if (isLastEnoughTime(lastTime, demoContext, configuration.getDemoKichenSeconds())) {
						status = 1;
						logger.info("<<<<<<<status to:" + status);
						lastTime = demoContext.getTimestamp();
						commands.add(generateEnterRoomCommand(entities));
					}
				}

			}
		}
		return commands;
	}
	
	// Triggered by timer
	public  List<ConfigurationCommand> execute() {
		return ConfigurationCommand.getEmptyCommand();
	}
	
	private ConfigurationCommand generateEnterRoomCommand(List<ConfigurationEntity> entities) {
		ConfigurationCommand command = new ConfigurationCommand("Master", ConfigurationType.POST);
		command.addEntity(new ConfigurationEntity(KICHEN_SLIDER_COMPONENT_ID, LEVEL_TWO));
		command.addEntity(new ConfigurationEntity(TABLE_SLIDER_COMPONENT_ID, LEVEL_ONE));
		command.addEntity(new ConfigurationEntity(OUTER_SLIDER_COMPONENT_ID, LEVEL_ONE));
		command.addEntity(new ConfigurationEntity(WALL_LIGHT_SLIDER_COMPONENT_ID, LEVEL_ONE));
		return command;
	}
	
	private ConfigurationCommand generateInKichenCommand(List<ConfigurationEntity> entities) {
		ConfigurationCommand command = new ConfigurationCommand("Master", ConfigurationType.POST);
		command.addEntity(new ConfigurationEntity(KICHEN_SLIDER_COMPONENT_ID, LEVEL_THREE));
		command.addEntity(new ConfigurationEntity(TABLE_SLIDER_COMPONENT_ID, LEVEL_THREE));
		command.addEntity(new ConfigurationEntity(OUTER_SLIDER_COMPONENT_ID, LEVEL_ONE));
		command.addEntity(new ConfigurationEntity(WALL_LIGHT_SLIDER_COMPONENT_ID, LEVEL_ONE));
		return command;
	}
	
	private ConfigurationCommand generateInTableConversation(List<ConfigurationEntity> entities) {
		ConfigurationCommand command = new ConfigurationCommand("Master", ConfigurationType.POST);
		command.addEntity(new ConfigurationEntity(KICHEN_SLIDER_COMPONENT_ID, LEVEL_TWO));
		command.addEntity(new ConfigurationEntity(TABLE_SLIDER_COMPONENT_ID, LEVEL_TWO));
		command.addEntity(new ConfigurationEntity(OUTER_SLIDER_COMPONENT_ID, LEVEL_THREE));
		command.addEntity(new ConfigurationEntity(WALL_LIGHT_SLIDER_COMPONENT_ID, LEVEL_THREE));
		return command;
	}
	
	private ConfigurationCommand generateTurnOffCommand(List<ConfigurationEntity> entities) {
		ConfigurationCommand command = new ConfigurationCommand("Master", ConfigurationType.POST, 2);
		command.addEntity(new ConfigurationEntity(KICHEN_SLIDER_COMPONENT_ID, LEVEL_ONE));
		command.addEntity(new ConfigurationEntity(TABLE_SLIDER_COMPONENT_ID, LEVEL_ONE));
		command.addEntity(new ConfigurationEntity(OUTER_SLIDER_COMPONENT_ID, LEVEL_ONE));
		command.addEntity(new ConfigurationEntity(WALL_LIGHT_SLIDER_COMPONENT_ID, LEVEL_ONE));
		return command;
	}
	
	private ConfigurationCommand generateCloseHueCommand() {
		ConfigurationCommand command = new ConfigurationCommand("Hue", ConfigurationType.PUT);
		command.addEntity(new HueEntity());
		return command;
	}
	
	private ConfigurationCommand generateOpenHueCommand() {
		ConfigurationCommand command = new ConfigurationCommand("Hue", ConfigurationType.PUT);
		command.addEntity(new HueEntity(255, 100, 18000));
		return command;
	}
	
	private boolean isEmpty(DemoFactor context) {
		if (context.ppnum1 == 0 && context.ppnum2 == 0 && context.ppnum3 == 0
				&& context.ppnum4 == 0 && context.ppnum5 == 0 && context.ppnum6 == 0) {
			return true;
		}
		
		return false;
	}
	
	private boolean isEnterRoom(DemoFactor context) {
		if(isEmpty(lastContext) && !isEmpty(context)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isPeopleExist(DemoFactor context) {
	
		if (context.ppnum1 > 0 || context.ppnum2 > 0 || context.ppnum3 > 0
				|| context.ppnum4 > 0 || context.ppnum5 > 0 || context.ppnum6 > 0) {
			return true;
		}
		
		return false;
	}
	
	private boolean isInKichen(DemoFactor context) {
		if (context.ppnum4 > 0 || context.ppnum6 > 0 || context.ppnum3 > 0) {
			return true;
		}
		
		return false;
	}
	
	private boolean isInTableConversation(DemoFactor context) {
		if (context.ppnum5 > 0) {
			return true;
		}
		
		return false;
	}
	
	private boolean isLastEnoughTime(DemoFactor oldContext, DemoFactor newContext, int seconds) {
		Long secondPast = (newContext.getTimestamp() - oldContext.getTimestamp());
		if (secondPast > seconds) {
			return true;
		}
		
		return false;
	}
	
	private boolean isLastEnoughTime(Long ltime, DemoFactor newContext, int seconds) {
		Long secondPast = (newContext.getTimestamp() - ltime);
		if (secondPast > seconds) {
			return true;
		}
		
		return false;
	}

	public void activate(Object model) {
		// TODO Auto-generated method stub
		
	}

	public void setup() {
		// TODO Auto-generated method stub
		
	}

	public void execute(ExecutionContext context) {
		// TODO Auto-generated method stub
		
	}

	public void clean(ExecutionContext context) {
		// TODO Auto-generated method stub
		
	}
}
