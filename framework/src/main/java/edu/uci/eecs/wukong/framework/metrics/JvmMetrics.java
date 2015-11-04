package edu.uci.eecs.wukong.framework.metrics;

import edu.uci.eecs.wukong.framework.api.metrics.MetricsRegistry;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ThreadInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import edu.uci.eecs.wukong.framework.api.metrics.Counter;
import edu.uci.eecs.wukong.framework.api.metrics.Gauge;
import edu.uci.eecs.wukong.framework.util.DaemanThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JvmMetrics extends MetricsHelper implements Runnable {
	private final static Logger LOGGER = LoggerFactory.getLogger(JvmMetrics.class);
	private final static String JVM_METRICS = "JVM-METRICS";
	private final static int M = 1024 * 1024;
	
	private MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
	private List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
	private ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
	private ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
	private Map<String, Counter> gcBeenCounters = new HashMap<String, Counter>();
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new DaemanThreadFactory(JVM_METRICS));
	
	private Gauge gMemNonHeapUsedM = newGauge("mem-non-heap-used-mb", 0.0F);
	private Gauge gMemNonHeapCommittedM = newGauge("mem-non-heap-committed-mb", 0.0F);
	private Gauge gMemNonHeapMaxM = newGauge("mem-non-heap-max-mb", 0.0F);
	private Gauge gMemHeapUsedM = newGauge("mem-heap-used-mb", 0.0F);
	private Gauge gMemHeapCommittedM = newGauge("mem-heap-committed-mb", 0.0F);
	private Gauge gMemHeapMaxM = newGauge("mem-heap-max-mb", 0.0F);
	private Gauge gThreadsNew = newGauge("threads-new", 0L);
	private Gauge gThreadsRunnable = newGauge("threads-runnable", 0L);
	private Gauge gThreadsBlocked = newGauge("treads-blocked", 0L);
	private Gauge gThreadsWaiting = newGauge("threads-waiting", 0L);
	private Gauge gThreadsTimedWaiting = newGauge("threads-timed-waiting", 0L);
	private Gauge gThreadsTerminated = newGauge("threads-terminated", 0L);
	private Counter cGcCount = newCounter("gc-count");
	private Counter cGcTimeMills = newCounter("gc-time-millis");
	

	public JvmMetrics(MetricsRegistry registry) {
		super(registry);
		// TODO Auto-generated constructor stub
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
		
		gThreadsNew.set(threadsNew);
		gThreadsRunnable.set(threadsRunnable);
		gThreadsBlocked.set(threadsBlocked);
		gThreadsWaiting.set(threadsWaiting);
		gThreadsTimedWaiting.set(threadsTimedWaiting);
		gThreadsTerminated.set(threadsTerminated);
	}
}
