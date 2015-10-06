package edu.uci.eecs.wukong.framework.context;

public interface ContextListener {

	public void onContextArrival(BaseContext context);
	
	public void onContextExpired(BaseContext context);
	
	public void onContextDeleted(BaseContext context);
}
