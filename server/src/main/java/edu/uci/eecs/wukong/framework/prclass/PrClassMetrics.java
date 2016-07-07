package edu.uci.eecs.wukong.framework.prclass;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.api.metrics.Counter;
import edu.uci.eecs.wukong.framework.api.metrics.Timer;
import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;
import edu.uci.eecs.wukong.framework.metrics.MetricsHelper;

import java.util.Map;
import java.util.HashMap;

public class PrClassMetrics extends MetricsHelper {
	private static final String UPDATE_SURFIX = "update";
	private static final String TIMER = "Timer";
	private static final String COUNTER = "Counter";
	
	private Map<String, Timer> prClassTimers;
	private Map<String, Counter> prClassCounters;

	public PrClassMetrics(MetricsRegistry registry) {
		super(registry);
		this.prClassTimers = new HashMap<String, Timer>();
		this.prClassCounters = new HashMap<String, Counter>();
	}
	
	public void addPrClassMeter(PrClass prClass) {
		if (prClass instanceof SimplePrClass) {
			String timerName = buildMetricsName(prClass, null, TIMER);
			prClassTimers.put(timerName, newTimer(timerName));
			String counterName = buildMetricsName(prClass, null, COUNTER);
			prClassCounters.put(counterName, newCounter(counterName));
		} else if (prClass instanceof EdgePrClass) {
			EdgePrClass pipelinePrClass = (EdgePrClass)prClass;
			if (pipelinePrClass.registerExtension() != null) {
				for (Extension extension : pipelinePrClass.registerExtension()) {
					String timerName = buildMetricsName(prClass, extension, TIMER);
					prClassTimers.put(timerName, newTimer(timerName));
					String counterName = buildMetricsName(prClass, extension, COUNTER);
					prClassCounters.put(counterName, newCounter(counterName));
				}
			}
		}
	}
	
	public Timer getTimer(PrClass prClass, Extension extension) {
		String name = buildMetricsName(prClass, extension, TIMER);
		return prClassTimers.get(name);
	}
	
	public Counter getCounter(PrClass prClass, Extension extension) {
		String name = buildMetricsName(prClass, extension, COUNTER);
		return prClassCounters.get(name);
	}
	
	private String buildMetricsName(PrClass prClass, Extension extension, String type) {
		if (extension != null) {
			// For pipeline prclass
			return prClass.getName() + '.' + prClass.getPortId() + '.' + extension.prefix() + "." + type;
		} else {
			// For simple prclass
			return prClass.getName() + '.' + prClass.getPortId() + '.' + UPDATE_SURFIX + "." + type;
		}
	}
}
