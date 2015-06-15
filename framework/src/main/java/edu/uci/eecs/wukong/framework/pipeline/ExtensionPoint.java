package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.extension.Extension;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

public abstract class ExtensionPoint<E extends Extension> {
	protected List<E> extensions;
	protected ExecutorService executor;
	protected Pipeline pipeline;
	
	public ExtensionPoint(Pipeline pipeline) {
		this.executor = Executors.newFixedThreadPool(5);
		this.extensions = new ArrayList<E>();
		this.pipeline = pipeline;
	}
	
	public void register(E extension) {
		extensions.add(extension);
	}
}
