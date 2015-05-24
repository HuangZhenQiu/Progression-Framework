package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.context.ExecutionContext;

public interface Extension<T> {
	
	public void setup();
	public void execute(ExecutionContext context);
	public void clean(ExecutionContext context);
}
