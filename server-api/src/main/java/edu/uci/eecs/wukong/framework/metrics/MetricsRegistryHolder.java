package edu.uci.eecs.wukong.framework.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsRegistryHolder implements ReadableMetricsRegistry {
	private final static Logger LOGGER = LoggerFactory.getLogger(MetricsRegistryHolder.class);
	
	private Set<ReadableMetricsRegistryListener> listeners;
	private ConcurrentHashMap<String, ConcurrentHashMap<String, Metrics>> metricsMap;
	
	public MetricsRegistryHolder() {
		listeners = new HashSet<ReadableMetricsRegistryListener>();
		metricsMap = new ConcurrentHashMap<String, ConcurrentHashMap<String, Metrics>>();
	}
	
	private ConcurrentHashMap<String, Metrics> putAndGetGroup(String group) {
		if (metricsMap.get(group) == null) {
			ConcurrentHashMap<String, Metrics> groupMap = new ConcurrentHashMap<String, Metrics> ();
			metricsMap.put(group,  groupMap);
		}
		
		return metricsMap.get(group);
	}

	@Override
	public Counter newCounter(String group, String name) {
		LOGGER.debug(String.format("Creating new counter %s %s", group, name));
		return newCounter(group, new Counter(name));
	}

	@Override
	public Counter newCounter(String group, Counter counter) {
		LOGGER.debug(String.format("Add new counter %s %s %s", group, counter.getName(), counter));
		putAndGetGroup(group).putIfAbsent(counter.getName(), counter);
		Counter created = (Counter) metricsMap.get(group).get(counter.getName());
		for (ReadableMetricsRegistryListener listener : listeners) {
			listener.onCounter(group, created);
		}
		
		return created;
	}

	@Override
	public <T> Gauge<T> newGauge(String group, String name, T value) {
		LOGGER.debug(String.format("Creating new guage %s %s", group, name));
		return newGauge(group, new Gauge(name, value));
	}

	@Override
	public <T> Gauge<T> newGauge(String group, Gauge<T> gauge) {
		LOGGER.debug(String.format("Add new gauge %s %s %s", group, gauge.getName(), gauge));
		putAndGetGroup(group).putIfAbsent(gauge.getName(), gauge);
		Gauge<T> created = (Gauge<T>) metricsMap.get(group).get(gauge.getName());
		for (ReadableMetricsRegistryListener listener : listeners) {
			listener.onGauge(group, created);
		}
		return created;
	}
	
	@Override
	public Meter newMeter(String group, String name) {
		LOGGER.debug(String.format("Creating new meter %s %s", group, name));
		return newMeter(group, new Meter(name));
	}
	
	@Override
	public Meter newMeter(String group, Meter meter) {
		LOGGER.debug(String.format("Add new meter %s %s %s", group, meter.getName(), meter));
		putAndGetGroup(group).putIfAbsent(meter.getName(), meter);
		
		Meter created = (Meter) metricsMap.get(group).get(meter.getName());
		for (ReadableMetricsRegistryListener listener : listeners) {
			listener.onMeter(group, created);
		}
		return created;
	}
	
	@Override
	public Timer newTimer(String group, String name) {
		LOGGER.debug(String.format("Creating new timer %s %s", group, name));
		return newTimer(group, new Timer(name));
	}
	
	@Override
	public Timer newTimer(String group, Timer timer) {
		LOGGER.debug(String.format("Add new timer %s %s %s", group, timer.getName(), timer));
		putAndGetGroup(group).putIfAbsent(timer.getName(), timer);
		Timer created = (Timer) metricsMap.get(group).get(timer.getName());
		for (ReadableMetricsRegistryListener listener : listeners) {
			listener.onTimer(group, created);
		}
		return created;
	}

	@Override
	public Set<String> getGroups() {
		return this.metricsMap.keySet();
	}

	@Override
	public Map<String, Metrics> getGroup(String group) {
		return this.metricsMap.get(group);
	}

	@Override
	public void register(ReadableMetricsRegistryListener listener) {
		this.listeners.add(listener);		
	}

	@Override
	public void unregister(ReadableMetricsRegistryListener listener) {
		this.listeners.remove(listener);
	}

}
