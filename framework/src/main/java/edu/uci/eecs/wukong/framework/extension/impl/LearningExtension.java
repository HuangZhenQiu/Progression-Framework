package edu.uci.eecs.wukong.framework.extension.impl;

import java.util.List;

import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public abstract class LearningExtension<T extends Number> extends AbstractExtension {
	private boolean ready;
	
	public LearningExtension(Plugin plugin) {
		super(plugin);
		this.ready = false;
		// TODO Auto-generated constructor stub
	}

	public abstract void apply(List<T> data, ExecutionContext context);
	
	public abstract Object train() throws Exception;

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
}
