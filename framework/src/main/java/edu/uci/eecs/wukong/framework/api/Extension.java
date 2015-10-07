package edu.uci.eecs.wukong.framework.api;


public interface Extension {
	
	public void setup();
	public void clean(ExecutionContext context);
}
