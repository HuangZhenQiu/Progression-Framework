package edu.uci.eecs.wukong.framework.wkpf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.metrics.Counter;
import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;
import edu.uci.eecs.wukong.framework.metrics.MetricsHelper;

public class WKPFMetrics extends MetricsHelper {
	private final static Logger LOGGER = LoggerFactory.getLogger(WKPFMetrics.class);
	
	protected Counter getWuClassCounter = newCounter("get-wuclass-count");
	protected Counter getWuObjectCounter = newCounter("get-wuobject-count");
	protected Counter reprogramCounter = newCounter("reprogram-count");
	protected Counter setPropertyCounter = newCounter("set=property-count");
	
	public WKPFMetrics(MetricsRegistry registry) {
		super(registry);
	}
}
