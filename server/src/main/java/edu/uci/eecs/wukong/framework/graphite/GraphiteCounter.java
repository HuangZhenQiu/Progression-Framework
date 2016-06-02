package edu.uci.eecs.wukong.framework.graphite;

import edu.uci.eecs.wukong.framework.api.metrics.Counter;

public class GraphiteCounter extends com.codahale.metrics.Counter {
	private final Counter counter;
	
	public GraphiteCounter(Counter counter) {
		this.counter = counter;
	}
	
	@Override
	public long getCount() {
		return counter.get();
	}
	
	@Override
	public void inc() {
		counter.inc();
	}
	
	@Override
	public void inc(long n) {
		counter.inc(n);
	}
	
	@Override
	public void dec() {
		counter.dec();
	}
	
	@Override
	public void dec(long n) {
		counter.dec(n);
	}
}
