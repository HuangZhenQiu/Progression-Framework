package edu.uci.eecs.wukong.framework.client;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;

/**
 * FactorClientListener is base class for integrating listeners of every type of pub/sub system.
 * Once receive any message from the source. It is required to add those messages into the 
 * global ConcurrentMap factors. It is the unique map thats stores the latest factor of each topic.
 * A ExecutionContext instance need to pick up factor values from the map.
 */
public abstract class FactorClientListener {
	protected static Gson gson = new Gson();
    /* Shared concurrent data structure of all FactorClientListener instances */
	protected ConcurrentMap<String, BaseFactor> factors;
	/* Shared listeners with scene manager */
	protected List<FactorListener> factorListeners;
	
	public FactorClientListener(ConcurrentMap<String, BaseFactor> factors, List<FactorListener> factorListeners) {
		this.factors = factors;
		this.factorListeners = factorListeners;
	}
	
	public void register(FactorListener listener) {
		this.factorListeners.add(listener);
	}
	
}
