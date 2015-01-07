package edu.uci.eecs.wukong.framework.context;

public interface ContextListener {

	public void onContextArrival(Context context);
	
	public void onContextExpired(Context context);
	
	public void onContextDeleted(Context context);
}
