package edu.uci.eecs.wukong.framework.pipeline;


import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;
import edu.uci.eecs.wukong.framework.api.metrics.Gauge;
import edu.uci.eecs.wukong.framework.api.metrics.Meter;
import edu.uci.eecs.wukong.framework.metrics.MetricsHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PipelineMetrics extends MetricsHelper {
    public static final String LAG_SURRFIX = "_queue_lag";
    public static final String ET_AVG_SURFIX = "_et_avr";
	// extension point name - wuobject - timer
	// protected Map<String, Map<WuObjectModel, Timer>> extensionTimers;
	// extension point name - current queue size
	protected Map<String, Gauge<Integer>> extensionQueueLagGauges;
	protected Map<String, Meter> extensionQueueLagMeters;
	
	
	public PipelineMetrics(MetricsRegistry registry) {
		super(registry);
		this.extensionQueueLagGauges = new ConcurrentHashMap<String, Gauge<Integer>>();
		this.extensionQueueLagMeters = new ConcurrentHashMap<String, Meter> ();
	}
	
	public void addExtensionGauge(String extension) {
		if (!extensionQueueLagGauges.containsKey(extension)) {
			Gauge<Integer> gauge = newGauge(extension + LAG_SURRFIX, 0);
			extensionQueueLagGauges.put(extension, gauge);
		}
	}
	
	public void setExtensonQueuLag(String extension, int value) {
		if (extensionQueueLagGauges.containsKey(extension)) {
			extensionQueueLagGauges.get(extension).set(value);
		}
	}
	
	public void addExtensionMeter(String extension) {
		if (!extensionQueueLagMeters.containsKey(extension)) {
			Meter meter = newMeter(extension + ET_AVG_SURFIX);
			extensionQueueLagMeters.put(extension, meter);
		}
	}
}
