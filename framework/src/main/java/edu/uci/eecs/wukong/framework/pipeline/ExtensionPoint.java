package edu.uci.eecs.wukong.framework.pipeline;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.Map;

import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.extension.AbstractExtension;

/**
 * A extension point is a stage of data processing pipeline. It contains 
 * a task queue, and thread pool that processes tasks.
 * 
 *
 * @param <E> Type extends AbstractExtension
 */
public abstract class ExtensionPoint<E extends AbstractExtension> {
	protected Map<PrClass, AbstractExtension> extensionMap;
	protected ExecutorService executor;
	protected Pipeline pipeline;
	
	public ExtensionPoint(Pipeline pipeline) {
		this.executor = Executors.newFixedThreadPool(5);
		this.extensionMap = new HashMap<PrClass, AbstractExtension>();
		this.pipeline = pipeline;
	}
	
	/**
	 * Add extension into extension point 
	 * 
	 * @param extension
	 */
	public synchronized void register(E extension) {
		extensionMap.put(extension.getPrClass(), extension);
	}
	
	/**
	 * Remove extension from extension point
	 * 
	 * @param extension
	 */
	public synchronized void unregister(E extension) {
		if (extensionMap.containsKey(extension.getPrClass())) {
			extensionMap.remove(extension.getPrClass());
		}
	}
}
