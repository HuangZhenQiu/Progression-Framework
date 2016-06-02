package edu.uci.eecs.wukong.framework.checkpoint;


import java.util.Map;
import java.util.HashMap;

import edu.uci.eecs.wukong.framework.factor.FactorClient;
import edu.uci.eecs.wukong.framework.factor.FactorClientFactory;

/**
 * To make the models learn within Progression server fault tolerant. PrClass developer can define
 * a checkpointable model, we will periodically checkpoint the model as a json into a PrObjects's 
 * topic. 
 * 
 */
public class CheckPointManager {
	private Map<String, CheckpointableModel<?>> modelMap;
	private FactorClient factorClient;
	private long currentPeriod;
	
	public CheckPointManager() {
		modelMap = new HashMap<String, CheckpointableModel<?>>();
		factorClient = FactorClientFactory.getFactorClient();
		currentPeriod = 1000;
	}
	
	public long getCurrentPeriod() {
		return this.currentPeriod;
	}
}
