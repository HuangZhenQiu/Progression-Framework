package edu.uci.eecs.wukong.framework;

import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

import edu.uci.eecs.wukong.framework.metrics.MetricsReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import edu.uci.eecs.wukong.framework.checkpoint.CheckPointManager;
import edu.uci.eecs.wukong.framework.metrics.reporter.GraphiteMetricsReporter;
import edu.uci.eecs.wukong.framework.monitor.MonitorManager;
import edu.uci.eecs.wukong.framework.model.StateModel;
import edu.uci.eecs.wukong.framework.pipeline.BasicPipeline;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.prclass.PluginManager;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;
import edu.uci.eecs.wukong.framework.state.StateManager;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.rpc.netty.CommunicationServer;
import edu.uci.eecs.wukong.rpc.netty.service.DataService;
import edu.uci.eecs.wukong.rpc.netty.service.ProgressionDataServiceFactory;

public class ProgressionServer extends AbstractServer{
	private static Logger logger = LoggerFactory.getLogger(ProgressionServer.class);
	private static boolean isTest = false;
	private static boolean isProgression = false;
	private CommunicationServer server;
	private PluginManager pluginManager;
	private MetricsReporter metricsReporter;
	private MonitorManager monitorManager;
	private FeatureChoosers featureChoosers;
	private Pipeline pipeline;
	private WKPF wkpf;

	public ProgressionServer(PeerInfo peerInfo, boolean isTest, boolean progression) {
		super(progression);
		if (isTest) {
			logger.info("Starting server in test model");
		}
		init(peerInfo);
		this.metricsReporter = new GraphiteMetricsReporter("ProgressionServer");
		this.wkpf = new WKPF(bufferManager, wkpfMetrics, progression);
		this.monitorManager = new MonitorManager(wkpf);
		this.checkpointManager = new CheckPointManager();
		this.systemStates = new SystemStates(monitorManager, checkpointManager);
		this.featureChoosers = new FeatureChoosers(bufferManager, wkpf);
		this.pipeline = new BasicPipeline(contextManager, featureChoosers, pipelineMetrics);	
		this.pluginManager = new PluginManager(wkpf, contextManager, pipeline, bufferManager, prClassMetrics, progression);
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
		
		// register the blocking data service
		BlockingService blockingDataService = DataService.BlockingDataService.newReflectiveBlockingService(new ProgressionDataServiceFactory.BlockingDataServiceImpl());
		logger.info("Register blokcking service: " + blockingDataService.getDescriptorForType().getName());
		server.registerBlockingService(false, blockingDataService);
		
		
		// register the nonblocking data service
		Service dataService = DataService.NonBlockingDataService.newReflectiveService(new ProgressionDataServiceFactory.NonblockingDataServiceImpl());
		logger.info("Register nonblocking service: " + dataService.getDescriptorForType().getName());
		server.registerService(false, dataService);
		logger.info("Data services are registered in server");
	}
	
	// start the progression server
	public void start() {
		try {
			StateModel model = this.stateManager.recover();
			this.wkpf.start(model);
			this.pluginManager.init(model);
			this.server.start();
			this.pipeline.start();
			this.jvmMetrics.start();
			this.monitorManager.start();
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
		this.monitorManager.close();
	}
	
	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("t", "test", false, "Run in test mode");
		options.addOption("p", "progression", false, "Run as progression server");
		CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("t")) {
				isTest = true;
			}
			if (cmd.hasOption("p")) {
				isProgression = true;
			}
			checkPath();
			PeerInfo peerInfo = new PeerInfo("localhost", 10000);
			ProgressionServer server = new ProgressionServer(peerInfo, isTest, isProgression);
			server.start();
			server.attachShutDownHook();
			
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "ant", options );
		}
	}

}
