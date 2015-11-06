package edu.uci.eecs.wukong.framework.metrics; 

import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;
import edu.uci.eecs.wukong.framework.api.metrics.Counter;
import edu.uci.eecs.wukong.framework.api.metrics.Gauge;

public abstract class MetricsHelper {
	private String group;
	private MetricsRegistry registry;
	private MetricsGroup metricsGroup;
	
	public MetricsHelper(MetricsRegistry registry) {
		this.group = this.getClass().getName();
		this.registry = registry;
		this.metricsGroup = new MetricsGroup(group, getPrefix(), registry);
	}
	
	public Counter newCounter(String name) {
		return metricsGroup.newCounter(name);
	}
	
	public <T> Gauge<T> newGauge(String name, T value) {
		return metricsGroup.newGauge(name, value);
	}
	protected String getPrefix() {
		return "";
	}
}