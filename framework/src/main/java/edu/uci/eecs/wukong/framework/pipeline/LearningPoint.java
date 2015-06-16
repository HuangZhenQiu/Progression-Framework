package edu.uci.eecs.wukong.framework.pipeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.event.Event;
import edu.uci.eecs.wukong.framework.event.IEvent;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.util.Configuration;

@SuppressWarnings("rawtypes")
public class LearningPoint extends ExtensionPoint<LearningExtension> implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(LearningPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private Map<LearningExtension, Event> lastEvent;
	private Queue<Event> events;
	
	public LearningPoint(Pipeline pipeline) {
		super(pipeline);
		this.events = new PriorityBlockingQueue<Event>();
		this.lastEvent = new HashMap<LearningExtension, Event>();
	}

	private class LearningTask implements Runnable{
		private LearningExtension<?> extension;
		private IEvent event;
		private Context currentContext;
		public LearningTask(LearningExtension extension, IEvent event, Context context) {
			this.extension = extension;
			this.event = event;
			this.currentContext = context;
		}
		
		public void run() {
			try {
				if (!extension.getPlugin().isOnline()) {
					extension.apply(event.getData(), currentContext);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e.toString());
			}
		}
		
	}

	public void run() {
		Event event = events.poll();
		
	}
}
