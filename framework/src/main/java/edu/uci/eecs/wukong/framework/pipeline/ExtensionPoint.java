package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.extension.Extension;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

public abstract class ExtensionPoint<E extends Extension> {
	protected List<E> extensions;
	protected ExecutorService executor;
	
	public ExtensionPoint() {
		executor = Executors.newFixedThreadPool(5);
		extensions = new ArrayList<E>();
	}
	
	public void register(E extension) {
		extensions.add(extension);
	}
}
