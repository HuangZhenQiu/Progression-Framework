package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public abstract class LearningExtension<T extends Number> extends AbstractExtension {
	private boolean ready;
	
	public LearningExtension(PrClass plugin) {
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
