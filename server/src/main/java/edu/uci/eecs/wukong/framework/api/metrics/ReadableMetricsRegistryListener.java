package edu.uci.eecs.wukong.framework.api.metrics;

public interface ReadableMetricsRegistryListener {
	void onCounter(String group, Counter counter);
	
	void onGauge(String group, Gauge<?> gauge);
	
	void onMeter(String group, Meter meter);
	
	void onTimer(String group, Timer timer);
}
