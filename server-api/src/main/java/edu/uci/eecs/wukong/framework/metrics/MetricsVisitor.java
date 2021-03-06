package edu.uci.eecs.wukong.framework.metrics;


/**
 * A MetricsVisitor can be used to process each metric in a {@link org.apache.samza.metrics.ReadableMetricsRegistry},
 * encapsulating the logic of what to be done with each metric in the counter and gauge methods.  This makes it easy
 * to quickly process all of the metrics in a registry.
 */
public abstract class MetricsVisitor {
	
	public abstract void counter(Counter counter);
	
	public abstract <T> void gauge(Gauge<T> gauge);
	
	public abstract void meter(Meter meter);
	
	public abstract void timer(Timer timer);
	
	public void visit(Metrics metrics) {
		if (metrics instanceof Counter) {
			counter((Counter) metrics);
		} else if (metrics instanceof Gauge<?>) {
			gauge((Gauge<?>) metrics);
		} else if (metrics instanceof Meter) {
			meter((Meter) metrics);
		} else if (metrics instanceof Timer) {
			timer((Timer) metrics);
		}
	}
}
