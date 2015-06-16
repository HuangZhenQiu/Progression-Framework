package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public abstract class LearningExtension<T extends Number> extends AbstractExtension<T> {
	private boolean ready;
	
	public LearningExtension(Plugin plugin) {
		super(plugin);
		this.ready = false;
		// TODO Auto-generated constructor stub
	}

	public abstract void apply(List<T> data, Context context);
	
	public abstract Object train() throws Exception;

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
}
