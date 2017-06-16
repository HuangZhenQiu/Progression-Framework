package edu.uci.eecs.wukong.framework.metrics;

import java.util.Map;
import java.util.Set;

/**
 * A ReadableMetricsRegistry is a {@link org.apache.samza.metrics.MetricsRegistry} that also
 * allows read access to the metrics for which it is responsible.
 */
public interface ReadableMetricsRegistry extends MetricsRegistry {
	public Set<String> getGroups();
	
	public Map<String, Metrics> getGroup(String group);
	
	public void register(ReadableMetricsRegistryListener listener);
	
	public void unregister(ReadableMetricsRegistryListener listener);
}
