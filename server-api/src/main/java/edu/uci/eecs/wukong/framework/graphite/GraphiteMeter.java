package edu.uci.eecs.wukong.framework.graphite;

import edu.uci.eecs.wukong.framework.metrics.Meter;

public class GraphiteMeter implements com.codahale.metrics.Metered {
    private Meter meter;
    
	public GraphiteMeter(Meter meter) {
		this.meter = meter;
	}

	@Override
	public long getCount() {
		return meter.getCount();
	}

	@Override
	public double getFifteenMinuteRate() {
		return meter.getMeanRate();
	}

	@Override
	public double getFiveMinuteRate() {
		return meter.getMeanRate();
	}

	@Override
	public double getMeanRate() {
		return meter.getMeanRate();
	}

	@Override
	public double getOneMinuteRate() {
		return meter.getMeanRate();
	}
}
