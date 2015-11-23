package edu.uci.eecs.wukong.framework.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.event.Event;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.util.Configuration;

@SuppressWarnings("rawtypes")
public class LearningExtensionPoint extends ExtensionPoint<LearningExtension> implements FactorListener, Runnable{
	private static Logger logger = LoggerFactory.getLogger(LearningExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private Queue<Event> events;
	
	public LearningExtensionPoint(Pipeline pipeline) {
		super(pipeline);
		this.events = new PriorityBlockingQueue<Event>();
	}
	
	
	public void dipatchModel(byte portId, Object model) {
		
	}

	private class LearningTask implements Runnable{
		private LearningExtension<?> extension;
		private Event event;
		private ExecutionContext contexts;
		public LearningTask(LearningExtension extension, Event event, ExecutionContext context) {
			this.extension = extension;
			this.event = event;
			this.contexts = context;
		}
		
		public void run() {
			try {
				if (!extension.getPrClass().isOnline() && !extension.isReady()) {
					if (event.getType().equals(Event.EventType.ENTITY)) {
						FeatureEntity entity = (FeatureEntity) event.getData();
						extension.apply(entity.getFeatures(), contexts);
						// Remove from 
						if (extension.isReady()) {
							Object object= extension.train();
							dipatchModel(extension.getPrClass().getPortId(), object);
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
			Event event = events.poll();
			if (event != null) {
				LearningExtension extension = (LearningExtension) this.extensionMap.get(event.getPrClass());
				if (extension != null) {
					this.executor.execute(new LearningTask(extension, event,
							pipeline.getCurrentContext(extension.getPrClass())));
				} else {
					logger.error("Cant't find learning extension for the PrClass instance: " + event.getPrClass());
				}
			}
		}
	}

	public void onFactorArrival(BaseFactor context) {
		// TODO Auto-generated method stub
		
	}

	public void onTopicExpired(BaseFactor context) {
		// TODO Auto-generated method stub
		
	}

	public void onTopicDeleted(BaseFactor context) {
		// TODO Auto-generated method stub
		
	}
}
