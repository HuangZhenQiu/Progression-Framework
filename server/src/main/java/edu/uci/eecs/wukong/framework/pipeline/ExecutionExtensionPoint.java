package edu.uci.eecs.wukong.framework.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.entity.ModelEntity;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.event.Event;
import edu.uci.eecs.wukong.framework.extension.AbstractExtension;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.PipelineUtil;

public class ExecutionExtensionPoint extends ExtensionPoint<AbstractExecutionExtension<? extends EdgePrClass>>
	implements FactorListener, Runnable {
	private static Logger logger = LoggerFactory.getLogger(ExecutionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private Map<EdgePrClass, TimerTask> pluginTaskMap;
	private Timer timer;

	
	public ExecutionExtensionPoint(Pipeline pipeline) {
		super(pipeline);
		this.pluginTaskMap = new HashMap<EdgePrClass, TimerTask>();
		this.timer = new Timer(true);
	}
	
	@Override
	public synchronized void register(AbstractExecutionExtension<? extends EdgePrClass> extension) {
		super.register(extension);
		if (extension instanceof TimerExecutable) {
			TimerExecutable executable = (TimerExecutable) extension;
			float internal = PipelineUtil.getIntervalFromMethodAnnotation(executable);
			ProgressionTimerTask timerTask = new ProgressionTimerTask(executable);
			timer.scheduleAtFixedRate(timerTask, 5000 + pluginTaskMap.size() * 50, new Float(internal * configuration.getExtensionTimerUnit()).longValue());
			pluginTaskMap.put(extension.getPrClass(), timerTask);
			logger.info("Registered Timer Executor for every " + internal + "seconds  for plugin "
					+ extension.getPrClass().getName() + " of port " + extension.getPrClass().getPortId());
		}
		logger.info("Registered Execution extension for plugin "
				+ extension.getPrClass().getName() + " of port " + extension.getPrClass().getPortId());
	}
	
	@Override
	public synchronized void unregister(AbstractExecutionExtension<? extends EdgePrClass> extension) {
		super.unregister(extension);
		// Cancel the timer taks for the plugin
		pluginTaskMap.get(extension.getPrClass()).cancel();
		logger.info("Unregistered Progression extension for plugin "
				+ extension.getPrClass().getName() + " of port " + extension.getPrClass().getPortId());
	}
	
	private class ProgressionTimerTask extends TimerTask {
		
		private TimerExecutable executable;

		public ProgressionTimerTask(TimerExecutable executable) {
			this.executable = executable;
		}
		
		@Override
		public void run() {
			try {
				executable.execute();
			} catch (Exception e) {
				logger.error("Exception catched for execution of " + executable.toString() + e.toString());
			}
		}
		
	}
	
	private class FactorTask implements Runnable{
		private AbstractExecutionExtension extension;
		private BaseFactor currentContext;
		public FactorTask(AbstractExecutionExtension<? extends EdgePrClass> extension, BaseFactor context) {
			this.extension = extension;
			this.currentContext = context;
		}
		
		public void run() {
			try {
				if (extension instanceof FactorExecutable) {
					FactorExecutable factorExecutable = (FactorExecutable) extension;
					factorExecutable.execute(currentContext);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e.toString());
			}
		}
		
	}
	
	public void run() {
		logger.debug("Execution Extension Point Starts to Run");
		try {
			Event event = eventQueue.poll();
			if (event != null) {
				if (event.getType().equals(Event.EventType.FACTOR)) {
					BaseFactor factor = (BaseFactor)event.getData();
					if(factor != null) {
						logger.info("Execution Extension Point is polling new context:" + factor.toString());
						for(Map.Entry<EdgePrClass, AbstractExtension<? extends EdgePrClass>> entry : this.extensionMap.entrySet()) {
							AbstractExecutionExtension<? extends EdgePrClass> extension =
									(AbstractExecutionExtension<? extends EdgePrClass>) entry.getValue();
							if (extension instanceof FactorExecutable) {
								if (extension.isSubcribedTopic(factor.getTopicId())) {
									this.executor.execute(new FactorTask(extension, factor));
								}
							}
						}
					}
				} else if (event.getType().equals(Event.EventType.MODEL)) {
					ModelEntity modelEntity = (ModelEntity) event.getData();
					AbstractExecutionExtension<? extends EdgePrClass> extension =
							(AbstractExecutionExtension<? extends EdgePrClass>) this.extensionMap.get(modelEntity.getPrClass());
					if (extension != null && extension instanceof Activatable) {
						((Activatable)extension).activate(modelEntity.getModel());
						extension.getPrClass().setOnline(true);
					} else {
						logger.error("Progression extension is not found for prClass :" + modelEntity.getPrClass());
					}
				} else if (event.getType().equals(Event.EventType.FEATURE)) {
					FeatureEntity featureEntity = (FeatureEntity) event.getData();
					AbstractExecutionExtension<? extends EdgePrClass> extension =
							(AbstractExecutionExtension<? extends EdgePrClass>) this.extensionMap.get(featureEntity.getPrClass());
					if (extension != null && extension instanceof Executable) {
						((Executable)extension).execute(featureEntity.getFeatures(), this.pipeline.getCurrentContext(featureEntity.getPrClass()));
					} else {
						logger.error("Progression extension is not found for prClass :" + featureEntity.getPrClass());
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error in Exection Extend Point " + e.toString());
		}
	}
	
	public void onFactorArrival(BaseFactor factor) {
		Event event = new Event(null, factor, Event.EventType.FACTOR, 1);
		this.eventQueue.put(event);
	}
	
	public void onTopicExpired(BaseFactor factor) {
		
	}
	
	public void onTopicDeleted(BaseFactor factor) {
		
	}
}
