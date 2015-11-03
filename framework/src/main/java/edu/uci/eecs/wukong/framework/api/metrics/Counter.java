package edu.uci.eecs.wukong.framework.api.metrics;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A counter is a {@link org.apache.samza.metrics.Metric} that represents a cumulative value.
 * For example, the number of messages processed since the container was started.
 */
public class Counter implements Metrics {
	private final String name;
	private final AtomicLong count;
	
	public Counter(String name) {
		this.name = name;
		this.count = new AtomicLong(0);
	}
	
	public long inc() {
		return inc(1);
	}
	
	public long inc(int n) {
		return count.addAndGet(n);
	}
	
	
	public long dec() {
		return dec(1);
	}

	public long dec(long n) {
		return count.addAndGet(0 - n);
	}
	
	public void set(long n) {
		count.set(n);
	}
	
	public void clear() {
		count.set(0);
	}
	
	public long get() {
		return count.get();
	}
	
	@Override
	public void visit(MetricsVisitor visitor) {
		visitor.counter(this);
	}
	
	public String toString() {
		return count.toString();
	}
}
