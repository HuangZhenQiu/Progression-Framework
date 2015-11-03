package edu.uci.eecs.wukong.framework.api.metrics;

public interface MetricsRegistry {

	  Counter newCounter(String group, String name);


	  Counter newCounter(String group, Counter counter);


	  <T> Gauge<T> newGauge(String group, String name, T value);


	  <T> Gauge<T> newGauge(String group, Gauge<T> value);
}
