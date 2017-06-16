package edu.uci.eecs.wukong.framework.graphite;

import edu.uci.eecs.wukong.framework.metrics.Gauge;

public class GraphiteGauge<T> implements com.codahale.metrics.Gauge {
	private Gauge<T> gauge;
	
	public GraphiteGauge(Gauge<T> gauge) {
		this.gauge = gauge;
	}
	
	public T getValue() {
		return gauge.get();
	}
	
	public void set(T n) {
		gauge.set(n);
	}
}
