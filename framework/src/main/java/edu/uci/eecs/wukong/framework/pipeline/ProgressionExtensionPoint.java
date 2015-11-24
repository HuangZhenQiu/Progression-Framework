package edu.uci.eecs.wukong.framework.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.entity.ModelEntity;
import edu.uci.eecs.wukong.framework.event.Event;
import edu.uci.eecs.wukong.framework.exception.ExtensionNotFoundException;
import edu.uci.eecs.wukong.framework.extension.AbstractExtension;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class ProgressionExtensionPoint extends ExtensionPoint<AbstractProgressionExtension>
	implements FactorListener, Runnable {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private Map<PrClass, TimerTask> pluginTaskMap;
	private Timer timer;
	
	public ProgressionExtensionPoint(Pipeline pipeline) {
		super(pipeline);
		this.pluginTaskMap = new HashMap<PrClass, TimerTask>();
		this.timer = new Timer(true);
	}
	
	@Override
	public synchronized void register(AbstractProgressionExtension extension) {
		super.register(extension);
		if (extension instanceof TimerExecutable) {
			ProgressionTimerTask timerTask = new ProgressionTimerTask((TimerExecutable)extension);
			timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);
			pluginTaskMap.put(extension.getPrClass(), timerTask);
		}
		logger.info("Registered Progression extension for plugin "
				+ extension.getPrClass().getName() + " of port " + extension.getPrClass().getPortId());
	}
	
	@Override
	public synchronized void unregister(AbstractProgressionExtension extension) {
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
			executable.execute();
		}
		
	}
	
	private class FactorTask implements Runnable{
		private AbstractProgressionExtension extension;
		private BaseFactor currentContext;
		public FactorTask(AbstractProgressionExtension extension, BaseFactor context) {
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
		while(true) {
			Event event = eventQueue.poll();
			if (event.getType().equals(Event.EventType.FACTOR)) {
				BaseFactor factor = (BaseFactor)event.getData();
				if(factor != null) {
					logger.info("Progression Extension Point is polling new context:" + factor.toString());
					for(Map.Entry<PrClass, AbstractExtension> entry : this.extensionMap.entrySet()) {
						AbstractProgressionExtension extension = (AbstractProgressionExtension) entry.getValue();
						if (extension instanceof FactorExecutable) {
							if (extension.isSubcribedTopic(factor.getTopicId())) {
								this.executor.execute(new FactorTask(extension, factor));
							}
						}
					}
				}
			} else if (event.getType().equals(Event.EventType.MODEL)) {
				ModelEntity modelEntity = (ModelEntity) event.getData();
				AbstractProgressionExtension extension = (AbstractProgressionExtension) this.extensionMap.get(modelEntity.getPrClass());
				if (extension != null && extension instanceof Activatable) {
					((Activatable)extension).activate(modelEntity.getModel());
					extension.getPrClass().setOnline(true);
				} else {
					logger.error("Progression extension is not found for prClass :" + modelEntity.getPrClass());
				}
			}
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
