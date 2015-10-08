package edu.uci.eecs.wukong.framework.factor;

public interface FactorListener {

	public void onFactorArrival(BaseFactor context);
	
	public void onTopicExpired(BaseFactor context);
	
	public void onTopicDeleted(BaseFactor context);
}
