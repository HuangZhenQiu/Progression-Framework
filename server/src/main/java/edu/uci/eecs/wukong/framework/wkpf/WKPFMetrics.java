package edu.uci.eecs.wukong.framework.wkpf;

import edu.uci.eecs.wukong.framework.metrics.Counter;
import edu.uci.eecs.wukong.framework.metrics.MetricsRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.metrics.MetricsHelper;

public class WKPFMetrics extends MetricsHelper {
	private final static Logger LOGGER = LoggerFactory.getLogger(WKPFMetrics.class);
	
	protected Counter getWuClassCounter = newCounter("get-wuclass-count");
	protected Counter getWuObjectCounter = newCounter("get-wuobject-count");
	protected Counter getLocationCounter = newCounter("get-location-count");
	protected Counter reprogramOpenCounter = newCounter("reprogram-open-count");
	protected Counter reprogramWriteCounter = newCounter("reprogram-write-count");
	protected Counter reprogramCommitCounter = newCounter("reprogram-commit-count");
	protected Counter reprogramErrorCounter = newCounter("reprogram-error-count");
	protected Counter setPropertyCounter = newCounter("set-property-count");
	protected Counter setLocationCounter = newCounter("set-location-count");
	protected Counter setPropertyErrorCounter = newCounter("set-proper-error-count");
	protected Counter writePropertyCounter = newCounter("write-property-count");
	protected Counter minitorCounter = newCounter("monitor-count");
	protected Counter propertyInitCounter = newCounter("propety-init-counter");
	
	public WKPFMetrics(MetricsRegistry registry) {
		super(registry);
	}
}
