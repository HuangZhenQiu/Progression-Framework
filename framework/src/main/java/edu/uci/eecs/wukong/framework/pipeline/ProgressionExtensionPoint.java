package edu.uci.eecs.wukong.framework.pipeline;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.entity.ConfigurationEntity;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ContextListener;

public class ProgressionExtensionPoint extends ExtensionPoint<ProgressionExtension> implements ContextListener,
	Runnable {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
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
			List<ConfigurationEntity> entities = extension.execute(currentContext);
			configurationManager.send(entities);
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
