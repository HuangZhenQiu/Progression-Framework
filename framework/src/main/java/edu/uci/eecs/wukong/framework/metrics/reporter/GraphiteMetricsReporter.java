package edu.uci.eecs.wukong.framework.metrics.reporter;

import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricFilter;

import edu.uci.eecs.wukong.framework.api.metrics.Counter;
import edu.uci.eecs.wukong.framework.api.metrics.Gauge;
import edu.uci.eecs.wukong.framework.api.metrics.Metrics;
import edu.uci.eecs.wukong.framework.api.metrics.MetricsReporter;
import edu.uci.eecs.wukong.framework.api.metrics.MetricsVisitor;
import edu.uci.eecs.wukong.framework.api.metrics.ReadableMetricsRegistry;
import edu.uci.eecs.wukong.framework.api.metrics.ReadableMetricsRegistryListener;
import edu.uci.eecs.wukong.framework.graphite.GraphiteCounter;
import edu.uci.eecs.wukong.framework.graphite.GraphiteGauge;
import edu.uci.eecs.wukong.framework.util.Configuration;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.HashMap;

import org.jivesoftware.smack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphiteMetricsReporter implements MetricsReporter {
	private static final Logger logger = LoggerFactory.getLogger(GraphiteMetricsReporter.class);
	private static final Configuration configuration = Configuration.getInstance();
	private Map<ReadableMetricsRegistry, ReadableMetricsRegistryListener> listeners;
	private Map<ReadableMetricsRegistry, String> sources;
	private MetricRegistry graphiteRegistry;
	private GraphiteReporter reporter;
	private final String reporterName;
	private final long reportPeriod;
	private static final long DEFAULT_GRAPHITE_REPORT_PERIOD_SEC = 30L;
	private static final int DEFAULT_GRAPHITE_PORT = 2003;

	private GraphiteMetricsReporter(String name) {
		this.reporterName = name;
		this.graphiteRegistry = new MetricRegistry();
		this.listeners = new HashMap<ReadableMetricsRegistry, ReadableMetricsRegistryListener>();
		this.sources =  new HashMap<ReadableMetricsRegistry, String>();
		final String host = configuration.getGraphiteHost();
		final int port = configuration.getGraphiteIP(DEFAULT_GRAPHITE_PORT);
		reportPeriod = configuration.getGraphiteReportPeriod(DEFAULT_GRAPHITE_REPORT_PERIOD_SEC);
		InetSocketAddress graphiteAddress = new InetSocketAddress(host, port);
		Graphite graphite = new Graphite(graphiteAddress);
		reporter = GraphiteReporter.forRegistry(graphiteRegistry)
				.filter(MetricFilter.ALL)
				.convertDurationsTo(TimeUnit.NANOSECONDS)
				.build(graphite);
	}

	@Override
	public void start() {
		logger.info("Starting Graphite Reporter");
		reporter.start(reportPeriod, TimeUnit.SECONDS);
		for (Map.Entry<ReadableMetricsRegistry, ReadableMetricsRegistryListener> entry : listeners.entrySet()) {
			final ReadableMetricsRegistry metricsRegistry = entry.getKey();
			ReadableMetricsRegistryListener metricsListener = entry.getValue();
			final String source = sources.get(metricsRegistry);
			for (final String group : metricsRegistry.getGroups()) {
				for (Metrics metrics : metricsRegistry.getGroup(group).values()) {
					metrics.visit(new MetricsVisitor() {

						@Override
						public void counter(Counter counter) {
							try {
								String counterName = getGraphiteMetricsName(group, source, counter.getName());
								logger.debug(String.format("Registering Graphite Counter: %s.", counterName));
								graphiteRegistry.register(counterName, new GraphiteCounter(counter));
							} catch (IllegalArgumentException exception) {
								logger.info("Exception while registring for onCounter: " + exception);
							}
						}

						@Override
						public <T> void gauge(Gauge<T> gauge) {
							try {
								String gaugeName = getGraphiteMetricsName(group, source, gauge.getName());
								logger.debug(String.format("Registering Graphite Gauge: %s.", gaugeName));
								graphiteRegistry.register(gaugeName, new GraphiteGauge(gauge));
							} catch (IllegalArgumentException exception) {
								logger.info("Exception while registring for onGauge: " + exception);
							}
							
						}
						
					});
				}
			}
		}
	}
	
	private String getGraphiteMetricsName(String group, String source, String name) {
		if (StringUtils.isEmpty(group) || StringUtils.isEmpty(name) || StringUtils.isEmpty(source)) {
			throw new IllegalArgumentException("Make sure group, source and name are defined");
		}
		
		return String.format("%s.%s.%s", getSafeValue(group), getSafeValue(source), getSafeValue(name));
	}
	
	private String getSafeValue(String name) {
		return name.replace(".", "_");
	}

	@Override
	public void register(String source, ReadableMetricsRegistry registry) {
		if (!this.listeners.containsKey(registry)) {
			ReadableMetricsRegistryListener metricsListener = new ReadableMetricsRegistryListener() {

				@Override
				public void onCounter(String group, Counter counter) {
					try {
						String counterName = getGraphiteMetricsName(group, source, counter.getName());
						logger.debug(String.format("Registering Graphite Counter: %s.", counterName));
						graphiteRegistry.register(counterName, new GraphiteCounter(counter));
					} catch (IllegalArgumentException exception) {
						logger.info("Exception while registring for onCounter: " + exception);
					}
				}

				@Override
				public void onGauge(String group, Gauge<?> gauge) {
					try {
						String gaugeName = getGraphiteMetricsName(group, source, gauge.getName());
						logger.debug(String.format("Registering Graphite Gauge: %s.", gaugeName));
						graphiteRegistry.register(gaugeName, new GraphiteGauge(gauge));
					} catch (IllegalArgumentException exception) {
						logger.info("Exception while registring for onGauge: " + exception);
					}
				}
				
			};
			listeners.put(registry, metricsListener);
			sources.put(registry, source);
		} else {
			logger.warn("Try to re-register a registry for source %s. Ignoring.", source);
		}
	}

	@Override
	public void stop() {
		try {
			for (Map.Entry<ReadableMetricsRegistry, ReadableMetricsRegistryListener> entry : listeners.entrySet()) {
				final ReadableMetricsRegistry registry = entry.getKey();
				final ReadableMetricsRegistryListener listener = entry.getValue();
				registry.unregister(listener);
				for (String group : registry.getGroups()) {
					for (String metricsName : registry.getGroup(group).keySet()) {
						graphiteRegistry.remove(metricsName);
					}
				}
			}
			reporter.stop();
		} catch (Exception exception) {
			logger.warn("Exception while stopping MetricsReporter: " + exception);
		}
	}
}
