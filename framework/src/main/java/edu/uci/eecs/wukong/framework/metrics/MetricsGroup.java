package edu.uci.eecs.wukong.framework.metrics;

import edu.uci.eecs.wukong.framework.api.metrics.Counter;
import edu.uci.eecs.wukong.framework.api.metrics.Gauge;
import edu.uci.eecs.wukong.framework.api.metrics.Meter;
import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;

public class MetricsGroup {
	protected final MetricsRegistry registry;
	protected final String groupName;
	protected final String prefix;
	
	public MetricsGroup(String groupName, String prefix, MetricsRegistry registry) {
		this.groupName = groupName;
		this.registry = registry;
		this.prefix = prefix;
	}
	
	public Counter newCounter(String name) {
		return registry.newCounter(groupName, (prefix + name).toLowerCase());
	}
	
	public <T> Gauge<T> newGauge(String name, T value) {
		return registry.newGauge(groupName, new Gauge<T>((prefix + name).toLowerCase(), value));
	}
	
	public Meter newMeter(String name) {
		return registry.newMeter(groupName, (prefix + name).toLowerCase());
	}
}
