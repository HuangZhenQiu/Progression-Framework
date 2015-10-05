package edu.uci.eecs.wukong.framework.api;

import edu.uci.eecs.wukong.framework.context.ExecutionContext;

public interface Extension {
	
	public void setup();
	public void clean(ExecutionContext context);
}
