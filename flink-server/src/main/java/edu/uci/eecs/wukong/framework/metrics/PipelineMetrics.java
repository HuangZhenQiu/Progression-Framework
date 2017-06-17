package edu.uci.eecs.wukong.framework.metrics;

/**
 * Created by hpeter on 6/16/17.
 */
public class PipelineMetrics extends MetricsHelper {

    protected Counter windowCalls = newCounter("window-count");
    protected Timer executionTime = newTimer("e2e-execution");
    protected Timer latencyTime = newTimer("e2e-latency");

    public PipelineMetrics(MetricsRegistry registry) {
        super(registry);
    }

    public void countWindow() {
        windowCalls.inc();
    }

    public void recordLatency(long latency) {
        latencyTime.update(latency);
    }

    public void recordExecutionTime(long time) {
        executionTime.update(time);
    }
}
