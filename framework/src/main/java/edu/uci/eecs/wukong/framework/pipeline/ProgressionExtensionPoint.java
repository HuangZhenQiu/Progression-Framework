package edu.uci.eecs.wukong.framework.pipeline;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.entity.Entity;
import edu.uci.eecs.wukong.framework.entity.HueEntity;
import edu.uci.eecs.wukong.framework.entity.ConfigurationReport;
import edu.uci.eecs.wukong.framework.exception.ExtensionNotFoundException;
import edu.uci.eecs.wukong.framework.extension.AbstractExtension;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ContextListener;

public class ProgressionExtensionPoint extends ExtensionPoint<ProgressionExtension> implements ContextListener,
	Runnable {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private ConfigurationManager configurationManager;
	private Queue<Context> contexts;
	
	public ProgressionExtensionPoint(ConfigurationManager configurationManager, Pipeline pipeline) {
		super(pipeline);
		this.configurationManager = configurationManager;
		contexts = new ConcurrentLinkedQueue<Context>();
	}
	
	public void applyModel(String appId, Object model) throws Exception {
		ProgressionExtension extension = (ProgressionExtension) this.extensionMap.get(appId);
		if (extension != null) {
			extension.activate(model);
			extension.getPlugin().setOnline(true);
		} else {
			throw new ExtensionNotFoundException("Progression extension is not found for app :" + appId);
		}	
	}
	
	private class ProgressionTask implements Runnable{
		private ProgressionExtension<?> extension;
		private Context currentContext;
		public ProgressionTask(ProgressionExtension extension, Context context) {
			this.extension = extension;
			this.currentContext = context;
		}
		
		public void run() {
			try {
				List<ConfigurationCommand> commands = extension.execute(currentContext);
				for (ConfigurationCommand command: commands) {
					List<Entity> entities = command.getEntities();
					if(!command.isDelayed()) {
						if(command.getTarget().equals("Master")) {
							configurationManager.sendMasterReport(command.getType(),
									new ConfigurationReport(configuration.getDemoApplicationId() , entities));
						} else {
							for(Entity entity : command.getEntities()) {
								configurationManager.sendHueConfiguration(command.getType(), (HueEntity)entity);
							}
						}
					} else {
						if(command.getTarget().equals("Master")) {
							for (Entity entity : entities) {
								configurationManager.sendMasterReport(command.getType(),
										new ConfigurationReport(configuration.getDemoApplicationId() , entity));
								Thread.sleep(command.getSeconds() * 1000);
							}
						} else {
							for(Entity entity : entities) {
								configurationManager.sendHueConfiguration(command.getType(), (HueEntity)entity);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e.toString());
			}
		}
		
	}
	
	public void run() {
		while(true) {
			Context context = contexts.poll();
			if(context != null) {
				logger.info("Progression Extension Point is polling new context:" + context.toString());
				for(Map.Entry<String, AbstractExtension> entry : this.extensionMap.entrySet()) {
					ProgressionExtension extension = (ProgressionExtension) entry.getValue();
					if (extension.isSubcribedTopic(context.getTopicId())) {
						this.executor.execute(new ProgressionTask(extension, context));
					}
				}
			}
		}
	}
	
	public void onContextArrival(Context context) {
		contexts.add(context);
	}
	
	public void onContextExpired(Context context) {
		
	}
	
	public void onContextDeleted(Context context) {
		
	}
}
