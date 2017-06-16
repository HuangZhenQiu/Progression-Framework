package edu.uci.eecs.wukong.framework.api;

import com.google.common.collect.ImmutableMap;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorClient;
import edu.uci.eecs.wukong.framework.factor.LocationFactor;

import java.util.Map;

public class ExecutionContext {
	private static String LOCATION = "Location";
	private static String HUMAN_ACTION = "Action";
	private final Map<String, BaseFactor> factors;
	private final FactorClient client;
	
	public ExecutionContext(Map<String, BaseFactor> contexts, FactorClient client) {
		this.client = client;
		this.factors = ImmutableMap.<String, BaseFactor>builder().putAll(contexts).build();
	}
	
	public LocationFactor getLocationContext() {
		return (LocationFactor)factors.get(LOCATION);
	}
	
	public BaseFactor getContext(String key) {
		return factors.get(key);
	}
	
	public void publish(String topic, BaseFactor factor) {
		this.client.publish(topic, factor);
	}
}
