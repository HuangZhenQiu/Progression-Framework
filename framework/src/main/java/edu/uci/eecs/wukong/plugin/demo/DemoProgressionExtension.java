package edu.uci.eecs.wukong.plugin.demo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.entity.ConfigurationEntity;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.DemoContext;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class DemoProgressionExtension implements ProgressionExtension<FeatureEntity> {
	private static Logger logger = LoggerFactory.getLogger(DemoProgressionExtension.class);
	private static Configuration configuration = Configuration.getInstance();
	private static String KICHEN_SLIDER_COMPONENT_ID = configuration.getKichenSliderId();
	private static String TABLE_SLIDER_COMPONENT_ID = configuration.getTableSliderId();
	private static String DEMO_SLIDER_COMPONENT_ID = configuration.getOuterSliderId();
	private static String WALL_LIGHT_SLIDER_COMPONENT_ID = configuration.getWallSliderId();
	private static int LEVEL_TWO = 40;
	private static int LEVEL_THREE = 70;
	private static int LEVEL_FOUR = 99;
	private static int status; // 1 kichen, 2 table, 3 general
	private DemoContext lastContext = null;

	// Triggered by general data pipeline
	public  List<ConfigurationEntity> execute(List<FeatureEntity> data, ExecutionContext context) {
		return new ArrayList<ConfigurationEntity>();
	}
	
	// Triggered by context switch
	public  List<ConfigurationEntity> execute(Context context) {
		logger.info("DemoProgressionExtension received new context");
		List<ConfigurationEntity> entities = new ArrayList<ConfigurationEntity>();
		if(context instanceof DemoContext) {
			DemoContext demoContext = (DemoContext) context;
			if (lastContext == null) {
				lastContext = demoContext;
			} else {
				if (!lastContext.equals(demoContext)) {
					if (isEnterRoom(demoContext)) {
						status = 3;
						return generateEnterRoomCommand(entities);
					}
					lastContext = demoContext;
				} else if (!lastContext.isTriggered()){
					if (isInKichen(demoContext) && status != 1) {
						lastContext.setTriggered(true);
						status = 1;
						return generateInKichenCommand(entities);
					}
					
					if (isInTableConversation(demoContext) && status != 2) {
						lastContext.setTriggered(true);
						status = 2;
						return generateInTableConversation(entities);
					}
					
					if (isPeopleExist(demoContext) && status != 3) {
						status = 3;
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} //Delay the message
						return generateEnterRoomCommand(entities);
					}
				}
			}
		}
		
		return entities;
	}
	
	// Triggered by timer
	public  List<ConfigurationEntity> execute() {
		return new ArrayList<ConfigurationEntity>();
	}
	
	private List<ConfigurationEntity> generateEnterRoomCommand(List<ConfigurationEntity> entities) {
		entities.add(new ConfigurationEntity(KICHEN_SLIDER_COMPONENT_ID, LEVEL_THREE));
		entities.add(new ConfigurationEntity(TABLE_SLIDER_COMPONENT_ID, LEVEL_THREE));
		entities.add(new ConfigurationEntity(DEMO_SLIDER_COMPONENT_ID, LEVEL_THREE));
		entities.add(new ConfigurationEntity(WALL_LIGHT_SLIDER_COMPONENT_ID, LEVEL_THREE));
		return entities;
	}
	
	private List<ConfigurationEntity> generateInKichenCommand(List<ConfigurationEntity> entities) {
		entities.add(new ConfigurationEntity(KICHEN_SLIDER_COMPONENT_ID, LEVEL_THREE));
		entities.add(new ConfigurationEntity(TABLE_SLIDER_COMPONENT_ID, LEVEL_FOUR));
		entities.add(new ConfigurationEntity(DEMO_SLIDER_COMPONENT_ID, LEVEL_THREE));
		entities.add(new ConfigurationEntity(WALL_LIGHT_SLIDER_COMPONENT_ID, LEVEL_THREE));
		return entities;
	}
	
	private List<ConfigurationEntity> generateInTableConversation(List<ConfigurationEntity> entities) {
		entities.add(new ConfigurationEntity(KICHEN_SLIDER_COMPONENT_ID, LEVEL_TWO));
		entities.add(new ConfigurationEntity(TABLE_SLIDER_COMPONENT_ID, LEVEL_FOUR));
		entities.add(new ConfigurationEntity(DEMO_SLIDER_COMPONENT_ID, LEVEL_TWO));
		entities.add(new ConfigurationEntity(WALL_LIGHT_SLIDER_COMPONENT_ID, LEVEL_TWO));
		return entities;
	}
	
	private boolean isEmpty(DemoContext context) {
		if (context.ppnum1 == 0 && context.ppnum2 == 0 && context.ppnum3 == 0
				&& context.ppnum4 == 0 && context.ppnum5 == 0 && context.ppnum6 == 0) {
			return true;
		}
		
		return false;
	}
	
	private boolean isEnterRoom(DemoContext context) {
		if(isEmpty(lastContext) && !isEmpty(context)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isPeopleExist(DemoContext context) {
	
		if (context.ppnum1 > 0 || context.ppnum2 > 0 || context.ppnum3 > 0
				|| context.ppnum4 > 0 || context.ppnum5 > 0 || context.ppnum6 > 0) {
			return true;
		}
		
		return false;
	}
	
	private boolean isInKichen(DemoContext context) {
		if (context.equals(lastContext)) {
			if (context.ppnum4 > 0 && isLastEnoughTime(lastContext, context, configuration.getDemoKichenSeconds())) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isInTableConversation(DemoContext context) {
		if (context.equals(lastContext)) {
			if (context.ppnum3 > 0 && isLastEnoughTime(lastContext, context, configuration.getDemoKichenSeconds())) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isLastEnoughTime(DemoContext oldContext, DemoContext newContext, int seconds) {
		Long secondPast = (newContext.getTimestamp() - oldContext.getTimestamp());
		if (secondPast > seconds) {
			return true;
		}
		
		return false;
	}
}
