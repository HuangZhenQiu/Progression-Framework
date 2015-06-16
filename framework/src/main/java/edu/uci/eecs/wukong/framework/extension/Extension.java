package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.context.ExecutionContext;

public interface Extension {
	
	public void setup();
	public void clean(ExecutionContext context);
}
