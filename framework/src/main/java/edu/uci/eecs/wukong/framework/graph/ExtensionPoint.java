package edu.uci.eecs.wukong.framework.graph;

import java.util.concurrent.PriorityBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;

import edu.uci.eecs.wukong.framework.api.Closable;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.event.Event;
import edu.uci.eecs.wukong.framework.event.Event.EventType;
import edu.uci.eecs.wukong.framework.entity.Entity;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.extension.AbstractExtension;

/**
 * A extension point is a stage of data processing pipeline. It contains 
 * a task queue, and thread pool that processes tasks.
 * 
 *
 * @param <E> Type extends AbstractExtension
 */
public abstract class ExtensionPoint<E extends AbstractExtension<? extends PipelinePrClass>> extends Node {
	private static Logger logger = LoggerFactory.getLogger(ExtensionPoint.class);
	protected Map<PipelinePrClass, AbstractExtension<?>> extensionMap;
	protected PriorityBlockingQueue<Event<?>> eventQueue;
	protected ExecutorService executor;
	protected Pipeline pipeline;
	
	public ExtensionPoint(Pipeline pipeline) {
		super(pipeline);
		this.executor = Executors.newFixedThreadPool(5);
		this.eventQueue = new PriorityBlockingQueue<Event<?>>();
		this.extensionMap = new HashMap<PipelinePrClass, AbstractExtension<?>>();
		this.pipeline = pipeline;
	}
	
	/**
	 * Add extension into extension point 
	 * 
	 * @param extension
	 */
	public synchronized void register(E extension) {
		try {
			if (extension instanceof Initiable) {
				((Initiable) extension).init();
			}
			extensionMap.put(extension.getPrClass(), extension);
		} catch (Exception e) {
			logger.error("Fail to register extension for plugin "
					+ extension.getPrClass() + ", base of exception: " + e.toString());
		}
	}
	
	/**
	 * Remove extension from extension point
	 * 
	 * @param extension
	 */
	public synchronized void unregister(E extension) {
		try {
			if (extension instanceof Closable) {
				((Closable) extension).close();
			}
			if (extensionMap.containsKey(extension.getPrClass())) {
				extensionMap.remove(extension.getPrClass());
			}
		} catch (Exception e) {
			logger.error("Fail to unregister extension for plugin "
					+ extension.getPrClass() + ", base of exception: " + e.toString());
		}
	}
	
	public final void append(Entity entity) {
		Event<Entity> event = new Event<Entity> (entity.getPrClass(), entity, EventType.FEATURE, 1 /* Temporary Solution*/);
		eventQueue.add(event);
	}
	
	public void shutdown() {
		this.executor.shutdown();
	}
	
	public int getQueueSize() {
		return this.eventQueue.size();
	}
}
