package edu.uci.eecs.wukong.framework.metrics;

import edu.uci.eecs.wukong.framework.util.DaemanThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.management.OperatingSystemMXBean;

import javax.management.MBeanServerConnection;
import java.io.IOException;
import java.lang.management.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JvmMetrics extends MetricsHelper implements Runnable {
	private final static Logger LOGGER = LoggerFactory.getLogger(JvmMetrics.class);
	private final static String JVM_METRICS = "JVM-METRICS";
	private final static float M = 1024 * 1024;

	private MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
	private MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
	private OperatingSystemMXBean osMBean;
	private List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
	private ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
	private ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
	private Map<String, Counter> gcBeanTimesCounters = new HashMap<String, Counter>();
	private Map<String, Counter> gcBeanMillsCounters = new HashMap<String, Counter>();
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new DaemanThreadFactory(JVM_METRICS));
	
	private Gauge<Float> gMemNonHeapUsedM = newGauge("mem-non-heap-used-mb", 0.0F);
	private Gauge<Float> gMemNonHeapCommittedM = newGauge("mem-non-heap-committed-mb", 0.0F);
	private Gauge<Float> gMemNonHeapMaxM = newGauge("mem-non-heap-max-mb", 0.0F);
	private Gauge<Float> gMemHeapUsedM = newGauge("mem-heap-used-mb", 0.0F);
	private Gauge<Float> gMemHeapCommittedM = newGauge("mem-heap-committed-mb", 0.0F);
	private Gauge<Float> gMemHeapMaxM = newGauge("mem-heap-max-mb", 0.0F);
	private Gauge<Long> gThreadsNew = newGauge("threads-new", 0L);
	private Gauge<Long> gThreadsRunnable = newGauge("threads-runnable", 0L);
	private Gauge<Long> gThreadsBlocked = newGauge("treads-blocked", 0L);
	private Gauge<Long> gThreadsWaiting = newGauge("threads-waiting", 0L);
	private Gauge<Long> gThreadsTimedWaiting = newGauge("threads-timed-waiting", 0L);
	private Gauge<Long> gThreadsTerminated = newGauge("threads-terminated", 0L);
	private Gauge<Double> processCPULoad = newGauge("process-cpu-load", 0.0);
	private Gauge<Double> systemCPULoad = newGauge("system-cpu-load", 0.0);

	private Counter cGcCount = newCounter("gc-count");
	private Counter cGcTimeMillis = newCounter("gc-time-millis");
	

	public JvmMetrics(MetricsRegistry registry) throws IOException {
		super(registry);
		osMBean = ManagementFactory.newPlatformMXBeanProxy(
				mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
	}
	
	public void start() {
		executor.scheduleWithFixedDelay(this, 0, 5, TimeUnit.SECONDS);
	}
	
	public void stop() {
		executor.shutdown();
	}

	@Override
	public void run() {
		updateMemoeryUsage();
		updateGcUsage();
		updateThreadUsage();
		updateCPULoad();
	}

	private void updateCPULoad() {
		processCPULoad.set(osMBean.getProcessCpuLoad());
		systemCPULoad.set(osMBean.getSystemCpuLoad());
	}
	
	private void updateMemoeryUsage() {
		MemoryUsage memHeap = memoryBean.getHeapMemoryUsage();
		MemoryUsage memNonHeap = memoryBean.getNonHeapMemoryUsage();
		gMemNonHeapUsedM.set(memNonHeap.getUsed() / M);
		gMemNonHeapCommittedM.set(memNonHeap.getCommitted() / M);
		gMemNonHeapMaxM.set(memNonHeap.getMax() / M);
		gMemHeapUsedM.set(memHeap.getUsed() / M);
		gMemHeapCommittedM.set(memHeap.getCommitted() / M);
		gMemHeapMaxM.set(memHeap.getMax() / M);
		
	}

	private void updateGcUsage() {
		long count = 0;
		long timeMillis = 0;
		for (GarbageCollectorMXBean bean : gcBeans) {
			long c = bean.getCollectionCount();
			long t = bean.getCollectionTime();
			Counter timeCounter = getGcTimeCounter(bean.getName());
			Counter milliCounter = getGcMilliCounter(bean.getName());
			timeCounter.inc(c - timeCounter.get());
			milliCounter.inc(t - milliCounter.get());
			count += c;
			timeMillis += t;
		}
		
		cGcCount.inc(count - cGcCount.get());
		cGcTimeMillis.inc(timeMillis - cGcTimeMillis.get());
	}
	
	private Counter getGcTimeCounter(String name) {
		if (!this.gcBeanTimesCounters.containsKey(name)) {
			Counter timeCounter = newCounter(name + "-gc-count");
			gcBeanTimesCounters.put(name, timeCounter);
		}
		
		return gcBeanTimesCounters.get(name);
	}
	
	private Counter getGcMilliCounter(String name) {
		if (!this.gcBeanMillsCounters.containsKey(name)) {
			Counter timeCounter = newCounter(name + "-gc-time-millis");
			gcBeanMillsCounters.put(name, timeCounter);
		}
		
		return gcBeanTimesCounters.get(name);
	}
	
	private void updateThreadUsage() {
		int threadsNew = 0;
		int threadsRunnable = 0;
		int threadsBlocked = 0;
		int threadsWaiting = 0;
		int threadsTimedWaiting = 0;
		int threadsTerminated = 0;
		long[] threadIds = threadBean.getAllThreadIds();
		
		for (ThreadInfo info : threadBean.getThreadInfo(threadIds)) {
			switch (info.getThreadState()) {
				case NEW :
					threadsNew += 1;
					break;
				case RUNNABLE:
					threadsRunnable += 1;
					break;
				case BLOCKED:
					threadsBlocked += 1;
					break;
				case WAITING:
					threadsWaiting += 1;
					break;
				case TIMED_WAITING:
					threadsTimedWaiting += 1;
					break;
				case TERMINATED:
					threadsTerminated += 1;
					break;
				
			
			}
		}
		
		gThreadsNew.set((long)threadsNew);
		gThreadsRunnable.set((long)threadsRunnable);
		gThreadsBlocked.set((long)threadsBlocked);
		gThreadsWaiting.set((long)threadsWaiting);
		gThreadsTimedWaiting.set((long)threadsTimedWaiting);
		gThreadsTerminated.set((long)threadsTerminated);
	}
}
