package edu.uci.eecs.wukong.framework.metrics;

public interface MetricsRegistry {

	  Counter newCounter(String group, String name);

	  Counter newCounter(String group, Counter counter);

	  <T> Gauge<T> newGauge(String group, String name, T value);

	  <T> Gauge<T> newGauge(String group, Gauge<T> value);
	  
	  Meter newMeter(String group, String name);
	  
	  Meter newMeter(String group, Meter meter);
	  
	  Timer newTimer(String group, String name);
	  
	  Timer newTimer(String group, Timer timer);
}
