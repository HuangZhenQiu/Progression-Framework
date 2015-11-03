package edu.uci.eecs.wukong.framework.api.metrics;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A Gauge is a {@link org.apache.samza.metrics.Metric} that wraps some instance of T in a thread-safe
 * reference and allows it to be set or retrieved.  Gauges record specific values over time.
 * For example, the current length of a queue or the size of a buffer.
 *
 * @param <T> Instance to be wrapped in the gauge for metering.
 */
public class Gauge<T> implements Metrics {
	private final String name;
	private AtomicReference<T> ref;
	
	public Gauge(String name, T value) {
		this.name = name;
		this.ref = new AtomicReference<T>(value);
	}
	
	public boolean compareAndSet(T expect, T update) {
		return ref.compareAndSet(expect, update);
	}
	
	public T set(T n) {
		return ref.getAndSet(n);
	}
	
	public String getName() {
		return name;
	}
	
	public void visit(MetricsVisitor visitor) {
		visitor.gauge(this);
	}
	
	public String toString() {
		T value = ref.get();
		return (value == null ) ? "" : value.toString();
	}
}
