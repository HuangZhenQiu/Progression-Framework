package edu.uci.eecs.wukong.framework.metrics;

public abstract class MetricsHelper {
	private String group;
	private MetricsRegistry registry;
	private MetricsGroup metricsGroup;
	
	public MetricsHelper(MetricsRegistry registry) {
		this.group = this.getClass().getSimpleName();
		this.registry = registry;
		this.metricsGroup = new MetricsGroup(group, getPrefix(), registry);
	}
	
	public Counter newCounter(String name) {
		return metricsGroup.newCounter(name);
	}
	
	public <T> Gauge<T> newGauge(String name, T value) {
		return metricsGroup.newGauge(name, value);
	}
	
	public Meter newMeter(String name) {
		return metricsGroup.newMeter(name);
	}
	
	public Timer newTimer(String name) {
		return metricsGroup.newTimer(name);
	}
	
	protected String getPrefix() {
		return "";
	}
}
