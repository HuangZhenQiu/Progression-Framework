package edu.uci.eecs.wukong.framework.buffer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.uci.eecs.wukong.framework.metrics.Gauge;
import edu.uci.eecs.wukong.framework.metrics.MetricsHelper;
import edu.uci.eecs.wukong.framework.metrics.MetricsRegistry;
import edu.uci.eecs.wukong.framework.model.NPP;

public class BufferMetrics extends MetricsHelper {

	protected Gauge<Integer> bufferCounter = newGauge("buffer-count", 0);
	protected Gauge<Integer> channelCounter = newGauge("channel-count", 0);
	protected Gauge<Integer> bufferTotalSize = newGauge("buffer-total-size", 0);
	protected Map<NPP, Gauge<Integer>> bufferSizeMap;
	
	public BufferMetrics(MetricsRegistry registry) {
		super(registry);
		this.bufferSizeMap = new ConcurrentHashMap<NPP, Gauge<Integer>>();
	}
	
	public Gauge<Integer> getBufferSizeGauge(NPP npp) {
		if (!bufferSizeMap.containsKey(npp)) {
			Gauge<Integer> guage = newGauge("buffer-size-" + npp.toString(), 0);
			bufferSizeMap.put(npp, guage);
		}
		
		return bufferSizeMap.get(npp);
	}
}
