package edu.uci.eecs.wukong.framework.factor;

import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;
import edu.uci.eecs.wukong.framework.api.metrics.Counter;
import edu.uci.eecs.wukong.framework.metrics.MetricsHelper;


public class FactorMetrics extends MetricsHelper {

	protected Counter subscribedFactorCounter = newCounter("subscribed-factor-count");
	protected Counter receivedFactorCounter = newCounter("received-factor-count");
	protected Counter publishedFactorCounter = newCounter("published-factor-count");
	
	public FactorMetrics(MetricsRegistry registry) {
		super(registry);
	}
}
