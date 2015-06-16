package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.extension.AbstractExtension;
import edu.uci.eecs.wukong.framework.extension.Extension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public abstract class ExtensionPoint<E extends AbstractExtension> {
	protected Map<String, AbstractExtension> extensionMap;
	protected ExecutorService executor;
	protected Pipeline pipeline;
	
	public ExtensionPoint(Pipeline pipeline) {
		this.executor = Executors.newFixedThreadPool(5);
		this.extensionMap = new HashMap<String, AbstractExtension>();
		this.pipeline = pipeline;
	}
	
	public void register(E extension) {
		extensionMap.put(extension.getAppId(), extension);
	}
}
