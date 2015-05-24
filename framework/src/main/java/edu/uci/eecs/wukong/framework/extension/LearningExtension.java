package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.context.ExecutionContext;

public interface LearningExtension<T extends Number> extends Extension<T> {

	public void apply(List<T> data, ExecutionContext context);
	
	public Object train() throws Exception;
}
