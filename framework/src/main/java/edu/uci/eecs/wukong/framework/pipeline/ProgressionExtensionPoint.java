package edu.uci.eecs.wukong.framework.pipeline;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ContextListener;
import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.entity.Entity;
import edu.uci.eecs.wukong.framework.entity.HueEntity;
import edu.uci.eecs.wukong.framework.entity.ConfigurationReport;
import edu.uci.eecs.wukong.framework.exception.ExtensionNotFoundException;
import edu.uci.eecs.wukong.framework.extension.Activatable;
import edu.uci.eecs.wukong.framework.extension.ContextExecutable;
import edu.uci.eecs.wukong.framework.extension.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.impl.AbstractExtension;
import edu.uci.eecs.wukong.framework.extension.impl.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.extension.impl.ProgressionExtension;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.plugin.Plugin;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class ProgressionExtensionPoint extends ExtensionPoint<AbstractProgressionExtension>
	implements ContextListener, Runnable {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private ConfigurationManager configurationManager;
	private Map<Plugin, TimerTask> pluginTaskMap;
	private Queue<Context> contexts;
	private Timer timer;
	
	public ProgressionExtensionPoint(ConfigurationManager configurationManager, Pipeline pipeline) {
		super(pipeline);
		this.configurationManager = configurationManager;
		this.contexts = new ConcurrentLinkedQueue<Context>();
		this.pluginTaskMap = new HashMap<Plugin, TimerTask>();
		this.timer = new Timer(true);
	}
	
	@Override
	public synchronized void register(AbstractProgressionExtension extension) {
		super.register(extension);
		if (extension instanceof TimerExecutable) {
			ProgressionTimerTask timerTask = new ProgressionTimerTask((TimerExecutable)extension);
			timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);
			pluginTaskMap.put(extension.getPlugin(), timerTask);
		}
	}
	
	@Override
	public synchronized void unregister(AbstractProgressionExtension extension) {
		super.unregister(extension);
		// Cancel the timer taks for the plugin
		pluginTaskMap.get(extension.getPlugin()).cancel();
	}
	
	public void applyModel(String appId, Object model) throws Exception {
		AbstractProgressionExtension extension = (AbstractProgressionExtension) this.extensionMap.get(appId);
		
		if (extension instanceof Activatable) {
			if (extension != null) {
				((Activatable)extension).activate(model);
				extension.getPlugin().setOnline(true);
			} else {
				throw new ExtensionNotFoundException("Progression extension is not found for app :" + appId);
			}	
		}
	}
	
	private class ProgressionTimerTask extends TimerTask {
		
		private TimerExecutable executable;

		public ProgressionTimerTask(TimerExecutable executable) {
			this.executable = executable;
		}
		
		@Override
		public void run() {
			executable.execute();
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
				for(Map.Entry<Plugin, AbstractExtension> entry : this.extensionMap.entrySet()) {
					ProgressionExtension extension = (ProgressionExtension) entry.getValue();
					if (extension instanceof ContextExecutable) {
						if (extension.isSubcribedTopic(context.getTopicId())) {
							this.executor.execute(new ProgressionTask(extension, context));
						}
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
