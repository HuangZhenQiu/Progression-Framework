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

import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.ContextExecutable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.entity.Entity;
import edu.uci.eecs.wukong.framework.entity.HueEntity;
import edu.uci.eecs.wukong.framework.entity.ConfigurationReport;
import edu.uci.eecs.wukong.framework.exception.ExtensionNotFoundException;
import edu.uci.eecs.wukong.framework.extension.AbstractExtension;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class ProgressionExtensionPoint extends ExtensionPoint<AbstractProgressionExtension>
	implements FactorListener, Runnable {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private ConfigurationManager configurationManager;
	private Map<PrClass, TimerTask> pluginTaskMap;
	private Queue<BaseFactor> factors;
	private Timer timer;
	
	public ProgressionExtensionPoint(ConfigurationManager configurationManager, Pipeline pipeline) {
		super(pipeline);
		this.configurationManager = configurationManager;
		this.factors = new ConcurrentLinkedQueue<BaseFactor>();
		this.pluginTaskMap = new HashMap<PrClass, TimerTask>();
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
		logger.info("Registered Progression extension for plugin "
				+ extension.getPlugin().getName() + " of port " + extension.getPlugin().getPortId());
	}
	
	@Override
	public synchronized void unregister(AbstractProgressionExtension extension) {
		super.unregister(extension);
		// Cancel the timer taks for the plugin
		pluginTaskMap.get(extension.getPlugin()).cancel();
		logger.info("Unregistered Progression extension for plugin "
				+ extension.getPlugin().getName() + " of port " + extension.getPlugin().getPortId());
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
		private BaseFactor currentContext;
		public ProgressionTask(ProgressionExtension extension, BaseFactor context) {
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
			BaseFactor factor = factors.poll();
			if(factor != null) {
				logger.info("Progression Extension Point is polling new context:" + factor.toString());
				for(Map.Entry<PrClass, AbstractExtension> entry : this.extensionMap.entrySet()) {
					ProgressionExtension extension = (ProgressionExtension) entry.getValue();
					if (extension instanceof ContextExecutable) {
						if (extension.isSubcribedTopic(factor.getTopicId())) {
							this.executor.execute(new ProgressionTask(extension, factor));
						}
					}
				}
			}
		}
	}
	
	public void onFactorArrival(BaseFactor factor) {
		factors.add(factor);
	}
	
	public void onTopicExpired(BaseFactor factor) {
		
	}
	
	public void onTopicDeleted(BaseFactor factor) {
		
	}
}
