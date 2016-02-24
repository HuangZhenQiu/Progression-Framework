package edu.uci.eecs.wukong.framework;

import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.uci.eecs.wukong.framework.api.metrics.MetricsReporter;
import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.buffer.BufferMetrics;
import edu.uci.eecs.wukong.framework.checkpoint.CheckPointManager;
import edu.uci.eecs.wukong.framework.jetty.JettyServer;
import edu.uci.eecs.wukong.framework.metrics.MetricsRegistryHolder;
import edu.uci.eecs.wukong.framework.metrics.JvmMetrics;
import edu.uci.eecs.wukong.framework.factor.SceneManager;
import edu.uci.eecs.wukong.framework.factor.FactorMetrics;
import edu.uci.eecs.wukong.framework.metrics.reporter.GraphiteMetricsReporter;
import edu.uci.eecs.wukong.framework.monitor.MonitorManager;
import edu.uci.eecs.wukong.framework.model.StateModel;
import edu.uci.eecs.wukong.framework.pipeline.BasicPipeline;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.pipeline.PipelineMetrics;
import edu.uci.eecs.wukong.framework.prclass.PrClassManager;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;
import edu.uci.eecs.wukong.framework.state.StateManager;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.framework.wkpf.WKPFMetrics;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.Constants;
import edu.uci.eecs.wukong.rpc.netty.CommunicationServer;
import edu.uci.eecs.wukong.rpc.netty.service.DataService;
import edu.uci.eecs.wukong.rpc.netty.service.ProgressionDataServiceFactory;

public class ProgressionServer {
	private static Logger logger = LoggerFactory.getLogger(ProgressionServer.class);
	private static Configuration configuration = Configuration.getInstance();
	private static boolean isTest = false;
	private MetricsRegistryHolder registryHolder;
	private CheckPointManager checkpointManager;
	private CommunicationServer server;
	private SceneManager contextManager;
	private BufferManager bufferManager;
	private JettyServer jettyServer;
	private PrClassManager pluginManager;
	private MetricsReporter metricsReporter;
	private MonitorManager monitorManager;
	private FeatureChoosers featureChoosers;
	private Pipeline pipeline;
	private WKPF wkpf;
	private JvmMetrics jvmMetrics;
	private PipelineMetrics pipelineMetrics;
	private StateManager stateManager;
	private SystemStates systemStates;
	
	public ProgressionServer(PeerInfo peerInfo, boolean isTest) {
		
		if (isTest) {
			logger.info("Starting server in test model");
		}
		init(peerInfo);
		this.registryHolder = new MetricsRegistryHolder();
		this.metricsReporter = new GraphiteMetricsReporter("ProgressionServer");
		this.jvmMetrics = new JvmMetrics(this.registryHolder);
		this.pipelineMetrics = new PipelineMetrics(this.registryHolder);
		BufferMetrics bufferMetrics = new BufferMetrics(this.registryHolder);
		FactorMetrics factorMetrics = new FactorMetrics(this.registryHolder);
		WKPFMetrics wkpfMetrics = new WKPFMetrics(this.registryHolder);
		PrClassMetrics prClassMetrics = new PrClassMetrics(this.registryHolder);
		this.bufferManager = new BufferManager(bufferMetrics);
		this.contextManager = new SceneManager(factorMetrics);
		this.wkpf = new WKPF(bufferManager, wkpfMetrics);
		this.monitorManager = new MonitorManager(wkpf);
		this.checkpointManager = new CheckPointManager();
		this.systemStates = new SystemStates(monitorManager, checkpointManager);
		this.jettyServer = new JettyServer();
		this.featureChoosers = new FeatureChoosers(bufferManager, wkpf);
		this.pipeline = new BasicPipeline(contextManager, featureChoosers, pipelineMetrics);	
		this.pluginManager = new PrClassManager(wkpf, contextManager, pipeline, bufferManager, prClassMetrics);
		this.stateManager = new StateManager(wkpf, pluginManager);
		this.wkpf.register(pluginManager);
	}
	
	/**
	 *  Server side of proto based RPC. It is not in used right, but will be used
	 *  for dynamic loading PrClass from app store or master repository. It is also
	 *  good channel to implement the streaming layer resource manager protocol that
	 *  go beyond the capability of current version WKPF.
	 */	
	private void init(PeerInfo peerInfo) {

		server = new CommunicationServer(peerInfo, false, false, false);
		
		//register the blocking data service
		BlockingService blockingDataService = DataService.BlockingDataService.newReflectiveBlockingService(new ProgressionDataServiceFactory.BlockingDataServiceImpl());
		logger.info("Register blokcking service: " + blockingDataService.getDescriptorForType().getName());
		server.registerBlockingService(false, blockingDataService);
		
		
		//register the nonblocking data service
		Service dataService = DataService.NonBlockingDataService.newReflectiveService(new ProgressionDataServiceFactory.NonblockingDataServiceImpl());
		logger.info("Register nonblocking service: " + dataService.getDescriptorForType().getName());
		server.registerService(false, dataService);
		logger.info("Data services are registered in server");
		
	}
	
	//start the progression server
	public void start() {
		try {
			StateModel model = this.stateManager.recover();
			this.wkpf.start(model);
			this.pluginManager.init(model);
			this.server.start();
			this.pipeline.start();
			this.jvmMetrics.start();
			this.pipelineMetrics.start();
			this.metricsReporter.register("ProgressionServer" + this.wkpf.getNetworkId(), registryHolder);
			this.metricsReporter.start();
			this.jettyServer.start();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Fail to start progression server.");
		}
	}
	
	public void shutdown() {
		this.server.shutdown();
		this.pipeline.shutdown();
		this.wkpf.shutdown();
		this.jettyServer.shutdown();
		this.jvmMetrics.stop();
		this.pipelineMetrics.stop();
		this.metricsReporter.stop();
	}
	
	public void attachShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread() {
			public void run() {
				shutdown();
			}
		});
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
	
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("t", "test", false, "Run in test mode");
		CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("t")) {
				isTest = true;
			}
			checkPath();
			PeerInfo peerInfo = new PeerInfo("localhost", 10000);
			ProgressionServer server = new ProgressionServer(peerInfo, isTest);
			server.start();
			server.attachShutDownHook();
			
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "ant", options );
		}
	}

}
