package edu.uci.eecs.wukong.framework.api.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class Meter implements Metrics {
	private  AtomicLong count;
	private  String name;
	private  long startTime;
	
	public Meter(String name) {
		this.name = name;
		this.count = new AtomicLong(0);
		this.startTime = System.currentTimeMillis();
	}
	
	public long getCount() {
		return count.get();
	}
	
	public void mark() {
		count.addAndGet(1);
	}
	
	public double getMeanRate() {
		if (getCount() == 0) {
			return 0;
		} else {
			final double elapsed = System.currentTimeMillis() - startTime;
			return getCount() / elapsed;
		}
	}
	
	public String getName() {
		return this.name;
	}
	
	@Override
	public void visit(MetricsVisitor visitor) {
		visitor.meter(this);
	}
}
