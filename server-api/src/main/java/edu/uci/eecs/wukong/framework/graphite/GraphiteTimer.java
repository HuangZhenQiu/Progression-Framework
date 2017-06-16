package edu.uci.eecs.wukong.framework.graphite;

import edu.uci.eecs.wukong.framework.metrics.Timer;

public class GraphiteTimer extends com.codahale.metrics.Timer {
	private Timer timer;
	
	public GraphiteTimer(Timer timer) {
		this.timer = timer;
	}
	
	@Override
	public long getCount() {
	    return 0;
	}

	@Override
	public double getFifteenMinuteRate() {
	    return timer.getSnapshot().getAverage();
	}

	@Override
	public double getFiveMinuteRate() {
	    return timer.getSnapshot().getAverage();
	}

	@Override
	public double getMeanRate() {
		return timer.getSnapshot().getAverage();
	}

	@Override
	public double getOneMinuteRate() {
	    return timer.getSnapshot().getAverage();
	}
}
