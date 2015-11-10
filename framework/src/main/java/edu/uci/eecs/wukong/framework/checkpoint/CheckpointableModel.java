package edu.uci.eecs.wukong.framework.checkpoint;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;

/**
 * CheckpointableModel is created and maintained by CheckPointManager. When it is created,
 * manager is responsible for fetching latest historical record to restore the model. It 
 * is usefully for PrClass migration, and fast recovery personalized model from failure.
 * 
 */
public class CheckpointableModel<T extends BaseFactor> {
	private String topic;
	private T model;
	
	protected CheckpointableModel(T model) {
		this.model = model;
	}
	
	/**
	 * Used by CheckPointManager to get the content of the model
	 * @return json representation of the model
	 */
	protected String checkpoint() {
		return "";
	}
	
	/**
	 * Used by CheckPointManager to restore the model from historical message
	 * @param content json representation of the model
	 */
	protected void restore(String content) {
		
	}
	
	public T getModel() {
		return model;
	}
}
