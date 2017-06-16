package edu.uci.eecs.wukong.framework.metrics;

/**
 * A MetricsReporter is the interface that different metrics sinks, such as JMX, Graphite, implement to receive
 * metrics from the Samza framework and Samza jobs.
 */
public interface MetricsReporter {
	void start();

	void register(String source, ReadableMetricsRegistry registry);

	void stop();
}
