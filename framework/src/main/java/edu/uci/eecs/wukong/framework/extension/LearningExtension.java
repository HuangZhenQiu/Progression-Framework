package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public abstract class LearningExtension<F extends Number, T extends PipelinePrClass> extends AbstractExtension<T> {
	private boolean ready;
	
	public LearningExtension(T plugin) {
		super(plugin);
		this.ready = false;
		// TODO Auto-generated constructor stub
	}

	public abstract void apply(List<F> data, ExecutionContext context);
	
	public abstract Object train() throws Exception;

	public boolean isReady() {
		return ready;
	}

	public void setReady(boolean ready) {
		this.ready = ready;
	}
}
