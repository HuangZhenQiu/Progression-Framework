package edu.uci.eecs.wukong.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.buffer.BufferMetrics;
import edu.uci.eecs.wukong.framework.checkpoint.CheckPointManager;
import edu.uci.eecs.wukong.framework.factor.FactorMetrics;
import edu.uci.eecs.wukong.framework.factor.SceneManager;
import edu.uci.eecs.wukong.framework.jetty.JettyServer;
import edu.uci.eecs.wukong.framework.metrics.JvmMetrics;
import edu.uci.eecs.wukong.framework.metrics.MetricsRegistryHolder;
import edu.uci.eecs.wukong.framework.pipeline.PipelineMetrics;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.state.StateManager;
import edu.uci.eecs.wukong.framework.util.Constants;
import edu.uci.eecs.wukong.framework.wkpf.WKPFMetrics;

public abstract class AbstractServer {
	private static Logger logger = LoggerFactory.getLogger(AbstractServer.class);
	protected MetricsRegistryHolder registryHolder;
	protected SceneManager contextManager;
	protected BufferManager bufferManager;
	protected JvmMetrics jvmMetrics;
	protected PipelineMetrics pipelineMetrics;
	protected BufferMetrics bufferMetrics;
	protected FactorMetrics factorMetrics;
	protected PrClassMetrics prClassMetrics;
	protected WKPFMetrics wkpfMetrics;
	protected StateManager stateManager;
	protected SystemStates systemStates;
	protected JettyServer jettyServer;
	protected CheckPointManager checkpointManager;
	
	public AbstractServer(boolean progression) {
		this.registryHolder = new MetricsRegistryHolder();
		this.jvmMetrics = new JvmMetrics(this.registryHolder);
		this.pipelineMetrics = new PipelineMetrics(this.registryHolder);
		this.bufferMetrics = new BufferMetrics(this.registryHolder);
		this.factorMetrics = new FactorMetrics(this.registryHolder);
		this.wkpfMetrics = new WKPFMetrics(this.registryHolder);
		this.prClassMetrics = new PrClassMetrics(this.registryHolder);
		this.jettyServer = new JettyServer();
		this.bufferManager = new BufferManager(bufferMetrics);
		this.contextManager = new SceneManager(factorMetrics);
	}
	
	public static void checkPath() {
		String value = System.getenv(Constants.Path.PROGRESSION_HOME);
		if (value != null) {
			logger.info("Using Progression Home :" + value);
		} else {
			logger.error("Exit because of environement variable Progression_Home is not set.");
			System.exit(-1);
		}
	}
	
	protected abstract void start();
	
	protected abstract void shutdown();
	
	protected void attachShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread() {
			public void run() {
				shutdown();
			}
		});
	}
}
