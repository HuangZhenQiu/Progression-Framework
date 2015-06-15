package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public abstract class LearningExtension<T extends Number> extends AbstractExtension<T> {

	public LearningExtension(Plugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public abstract void apply(List<T> data, ExecutionContext context);
	
	public abstract Object train() throws Exception;
}
