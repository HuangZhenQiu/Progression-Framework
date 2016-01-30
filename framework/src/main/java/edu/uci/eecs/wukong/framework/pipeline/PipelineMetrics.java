package edu.uci.eecs.wukong.framework.pipeline;


import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.api.metrics.Gauge;
import edu.uci.eecs.wukong.framework.metrics.MetricsHelper;
import edu.uci.eecs.wukong.framework.util.DaemanThreadFactory;

import java.lang.Runnable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PipelineMetrics extends MetricsHelper implements Runnable{
	private static final String PIPELINE_METRICS_COLLECTOR = "PipelineMetricsCollector";
    public static final String LAG_SURRFIX = "_queue_lag";
    private ScheduledExecutorService executor =
    		Executors.newScheduledThreadPool(1, new DaemanThreadFactory(PIPELINE_METRICS_COLLECTOR));
    private Map<String, ExtensionPoint> extensionPointMap;
	// extension point name - wuobject - timer
	// protected Map<String, Map<WuObjectModel, Timer>> extensionTimers;
	// extension point name - current queue size
	protected Map<String, Gauge<Integer>> extensionQueueLagGauges;
	
	
	public PipelineMetrics(MetricsRegistry registry) {
		super(registry);
		this.extensionPointMap = new HashMap<String, ExtensionPoint>();
		this.extensionQueueLagGauges = new ConcurrentHashMap<String, Gauge<Integer>>();
	}
	
	public void start() {
		executor.scheduleWithFixedDelay(this, 0, 5, TimeUnit.SECONDS);
	}
	
	public void stop() {
		executor.shutdown();
	}
	
	public void addExtensionGauge(ExtensionPoint entensionPoint) {
		String pointName = entensionPoint.getClass().getSimpleName();
		if (!extensionQueueLagGauges.containsKey(pointName)) {
			Gauge<Integer> gauge = newGauge(pointName + LAG_SURRFIX, 0);
			extensionQueueLagGauges.put(pointName, gauge);
			extensionPointMap.put(pointName, entensionPoint);
		}
	}
	
	public void setExtensonQueuLag(String extension, int value) {
		if (extensionQueueLagGauges.containsKey(extension)) {
			extensionQueueLagGauges.get(extension).set(value);
		}
	}
	
	@Override
	public void run() {
		updateQueueLags();
	}
	
	private void updateQueueLags() {
		for (Entry<String, Gauge<Integer>> entry: extensionQueueLagGauges.entrySet()) {
			entry.getValue().set(extensionPointMap.get(entry.getKey()).getQueueSize());
		}
	}
}
