package edu.uci.eecs.wukong.framework.yarn;

import com.google.common.annotations.VisibleForTesting;
import com.uber.m3.client.Scope;
import com.uber.m3.client.Scopes;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.MetricConfig;
import org.apache.flink.metrics.reporter.AbstractReporter;
import org.apache.flink.metrics.reporter.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class M3MetricReporter extends AbstractReporter implements Scheduled {
    private static final String CONFIG_KEY = "tags";
    private static final Logger LOGGER = LoggerFactory.getLogger(M3MetricReporter.class);

    private Scope scope;

    @VisibleForTesting
    static String clean(String s) {
        return s.replaceAll("[>:,\\.\\s]", "");
    }

    // Get the container hostname, it will be a tag of the metrics
    static String getHostName() {
        try {
            InetAddress addr;
            addr = InetAddress.getLocalHost();
            return addr.getHostName();
        } catch (UnknownHostException ex) {
            return "unknown";
        }
    }

    private static com.uber.m3.client.MetricConfig translateConfig(MetricConfig config) {
        return new com.uber.m3.client.MetricConfig.Builder()
                .setCommonTags(extractTags(config))
                .setGenerateTagsFromEnv(false)
                .build();
    }

    @VisibleForTesting
    static Map<String, String> extractTags(MetricConfig config) {
        Map<String, String> tags = new HashMap<>();
        tags.put("host", getHostName());

        for (String pair : config.getString(CONFIG_KEY, "").split(",")) {
            if (pair.length() > 0) {
                String[] kv = pair.split("->");
                tags.put(kv[0].trim(), kv[1].trim());
            }
        }
        LOGGER.info("tags = {}", tags.toString());
        return tags;
    }

    @Override
    public void open(MetricConfig config) {
        this.scope = Scopes.getScopeForConfig(translateConfig(config));
        LOGGER.info("M3 Scope is :" + scope.toString());
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public String filterCharacters(String in) {
        return in;
    }

    @Override
    public void report() {
        for (Map.Entry<Gauge<?>, String> entry : getGauges().entrySet()) {
            Gauge<?> g = entry.getKey();
            String name = clean(entry.getValue());
            Object at = g.getValue();
            if (at instanceof Long) {
                scope.gauge(name, (Long) at);
            } else if (at instanceof Integer) {
                scope.gauge(name, (Integer) at);
            } else if (at instanceof Double) {
                scope.gauge(name, (Double) at);
            } else if (at instanceof Float) {
                scope.gauge(name, (Float) at);
            }
        }

        for (Map.Entry<Counter, String> entry : getCounters().entrySet()) {
            String name = clean(entry.getValue());
            scope.count(name, entry.getKey().getCount());
        }
    }

    private Map<Gauge<?>, String> getGauges() {
        return gauges;
    }

    private Map<Counter, String> getCounters() {
        return counters;
    }

    protected Map<Histogram, String> getHistograms() {
        return histograms;
    }
}
