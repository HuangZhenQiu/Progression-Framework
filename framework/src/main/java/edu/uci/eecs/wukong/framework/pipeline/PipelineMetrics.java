package edu.uci.eecs.wukong.framework.pipeline;


import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;
import edu.uci.eecs.wukong.framework.api.metrics.Timer;
import edu.uci.eecs.wukong.framework.api.metrics.Gauge;
import edu.uci.eecs.wukong.framework.metrics.MetricsHelper;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PipelineMetrics extends MetricsHelper {
     
	// extension point name - wuobject - timer
	protected Map<String, Map<WuObjectModel, Timer>> extensionTimers;
	// extension point name - current queue size
	protected Map<String, Gauge<Integer>> extensionQueueLags;
	
	
	public PipelineMetrics(MetricsRegistry registry) {
		super(registry);
	}

}
