package edu.uci.eecs.wukong.framework.factor;

public interface FactorListener {

	public void onContextArrival(BaseFactor context);
	
	public void onContextExpired(BaseFactor context);
	
	public void onContextDeleted(BaseFactor context);
}
