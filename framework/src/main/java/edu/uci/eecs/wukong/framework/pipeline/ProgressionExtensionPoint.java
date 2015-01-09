package edu.uci.eecs.wukong.framework.pipeline;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.entity.ConfigurationEntity;
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
	
	private class ProgressionTask implements Runnable{
		private ProgressionExtension<?> extension;
		private Context currentContext;
		public ProgressionTask(ProgressionExtension extension, Context context) {
			this.extension = extension;
			this.currentContext = context;
		}
		
		public void run() {
			try {
				ConfigurationCommand command = extension.execute(currentContext);
				if(!command.isDelayed()) {
					List<ConfigurationEntity> entities = command.getEntities();
					configurationManager.send(configuration.getDemoApplicationId() , entities);
				} else {
					for (ConfigurationEntity entity : command.getEntities()) {
						configurationManager.send(configuration.getDemoApplicationId() , entity);
						Thread.sleep(command.getSeconds() * 1000);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e.toString());
			}
		}
		
	}
	
	public ProgressionExtensionPoint(ConfigurationManager configurationManager) {
		this.configurationManager = configurationManager;
		contexts = new ConcurrentLinkedQueue<Context>();
	}
	
	public void run() {
		while(true) {
			Context context = contexts.poll();
			if(context != null) {
				logger.info("Progression Extension Point is polling new context:" + context.toString());

				for(ProgressionExtension progressionExtension : extensions) {
					this.executor.execute(new ProgressionTask(progressionExtension, context));
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
