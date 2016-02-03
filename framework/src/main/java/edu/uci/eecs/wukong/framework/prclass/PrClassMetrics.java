package edu.uci.eecs.wukong.framework.prclass;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.api.metrics.Timer;
import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;
import edu.uci.eecs.wukong.framework.metrics.MetricsHelper;

import java.util.Map;
import java.util.HashMap;

public class PrClassMetrics extends MetricsHelper {
	private static final String UPDATE_SURFIX = "update";
	
	private Map<String, Timer> prClassMeter;

	public PrClassMetrics(MetricsRegistry registry) {
		super(registry);
		this.prClassMeter = new HashMap<String, Timer>();
	}
	
	public void addPrClassMeter(PrClass prClass) {
		if (prClass instanceof SimplePrClass) {
			String name = buildMetricsName(prClass, null);
			Timer meter = newTimer(name);
			prClassMeter.put(name, meter);
		} else if (prClass instanceof PipelinePrClass) {
			PipelinePrClass pipelinePrClass = (PipelinePrClass)prClass;
			for (Extension extension : pipelinePrClass.registerExtension()) {
				String name = buildMetricsName(prClass, extension);
				Timer meter = newTimer(name);
				prClassMeter.put(name, meter);
			}
		}
	}
	
	public Timer getTimer(PrClass prClass, Extension extension) {
		String name = buildMetricsName(prClass, extension);
		return prClassMeter.get(name);
	}
	
	private String buildMetricsName(PrClass prClass, Extension extension) {
		if (extension != null) {
			// For pipeline prclass
			return prClass.getName() + '.' + prClass.getPortId() + '.' + extension.prefix();
		} else {
			// For simple prclass
			return prClass.getName() + '.' + prClass.getPortId() + '.' + UPDATE_SURFIX;
		}
	}
}
