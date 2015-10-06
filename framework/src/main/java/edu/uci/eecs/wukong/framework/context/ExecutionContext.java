package edu.uci.eecs.wukong.framework.context;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class ExecutionContext {
	private static String LOCATION = "Location";
	private static String HUMAN_ACTION = "Action";
	private Map<String, BaseContext> contexts;
	
	public ExecutionContext() {
		contexts = new HashMap<String, BaseContext>();
	}
	
	public ExecutionContext(Map<String, BaseContext> contexts) {
		contexts = ImmutableMap.<String, BaseContext>builder().putAll(contexts).build();
	}
	
	public LocationContext getLocationContext() {
		return (LocationContext)contexts.get(LOCATION);
	}
	
	public BaseContext getContext(String key) {
		return contexts.get(key);
	}
	
	public void addContext(BaseContext context) {
		contexts.put(context.getTopicId(), context);
	}
}
