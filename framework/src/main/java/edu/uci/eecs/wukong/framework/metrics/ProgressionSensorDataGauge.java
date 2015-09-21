package edu.uci.eecs.wukong.framework.metrics;

import com.codahale.metrics.Gauge;
import edu.uci.eecs.wukong.framework.wkpf.Model.MonitorData;

/**
 * Codehale Metric Gauge for Sensor data
 *
 */
public class ProgressionSensorDataGauge implements Gauge<Double> {
	private MonitorData data;
	
	public ProgressionSensorDataGauge(MonitorData data) {
		this.data = data;
	}
	
	public Double getValue() {
		return data.getValue();
	}
}
