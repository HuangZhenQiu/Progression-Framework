package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.extension.Extension;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

public abstract class ExtensionPoint<E extends Extension> {
	private List<E> extensions;
	protected ExecutorService executor;
	
	public ExtensionPoint() {
		executor = Executors.newFixedThreadPool(5);
	}
	
	public void register(E extension) {
		extensions.add(extension);
	}
}
