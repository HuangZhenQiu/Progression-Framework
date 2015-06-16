package edu.uci.eecs.wukong.framework.context;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ExecutionContext {
	private static String LOCATION = "Location";
	private static String HUMAN_ACTION = "Action";
	private Map<String, Context> contexts;
	
	public ExecutionContext() {
		contexts = new HashMap<String, Context>();
	}
	
	public ExecutionContext(Map<String, Context> contexts) {
		contexts = ImmutableMap.<String, Context>builder().putAll(contexts).build();
	}
	
	public LocationContext getLocationContext() {
		return (LocationContext)contexts.get(LOCATION);
	}
	
	public Context getContext(String key) {
		return contexts.get(key);
	}
}
