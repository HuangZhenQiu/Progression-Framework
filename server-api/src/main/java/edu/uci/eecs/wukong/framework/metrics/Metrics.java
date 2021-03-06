package edu.uci.eecs.wukong.framework.metrics;

/**
 * 
 * Metric Class that allows metrics visitors to visit it to get its information
 *
 */
public interface Metrics {
	void visit(MetricsVisitor visitor);
}
