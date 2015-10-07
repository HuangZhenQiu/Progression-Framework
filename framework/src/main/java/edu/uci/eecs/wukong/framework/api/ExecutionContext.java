package edu.uci.eecs.wukong.framework.api;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.LocationFactor;

public class ExecutionContext {
	private static String LOCATION = "Location";
	private static String HUMAN_ACTION = "Action";
	private Map<String, BaseFactor> contexts;
	
	public ExecutionContext() {
		contexts = new HashMap<String, BaseFactor>();
	}
	
	public ExecutionContext(Map<String, BaseFactor> contexts) {
		contexts = ImmutableMap.<String, BaseFactor>builder().putAll(contexts).build();
	}
	
	public LocationFactor getLocationContext() {
		return (LocationFactor)contexts.get(LOCATION);
	}
	
	public BaseFactor getContext(String key) {
		return contexts.get(key);
	}
	
	public void addContext(BaseFactor context) {
		contexts.put(context.getTopicId(), context);
	}
}
