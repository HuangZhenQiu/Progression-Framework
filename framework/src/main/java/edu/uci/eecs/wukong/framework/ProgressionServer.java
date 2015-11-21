package edu.uci.eecs.wukong.framework;

import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.manager.PluginManager;
import edu.uci.eecs.wukong.framework.manager.SceneManager;
import edu.uci.eecs.wukong.framework.monitor.MonitorManager;
import edu.uci.eecs.wukong.framework.model.StateModel;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;
import edu.uci.eecs.wukong.framework.state.StateManager;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.rpc.netty.CommunicationServer;
import edu.uci.eecs.wukong.rpc.netty.service.DataService;
import edu.uci.eecs.wukong.rpc.netty.service.ProgressionDataServiceFactory;

public class ProgressionServer {
	private static Logger logger = LoggerFactory.getLogger(ProgressionServer.class);
    private CommunicationServer server;
	private SceneManager contextManager;
	private BufferManager bufferManager;
	private PluginManager pluginManager;
	private StateManager stateManager;
	private MonitorManager monitorManager;
	private FeatureChoosers featureChoosers;
	private Pipeline pipeline;
	private WKPF wkpf;
	
	public ProgressionServer(PeerInfo peerInfo) {
		
		init(peerInfo);
		this.bufferManager = new BufferManager();
		this.contextManager = new SceneManager();
		this.wkpf = new WKPF(bufferManager);
		this.monitorManager = new MonitorManager(wkpf);
		this.featureChoosers = new FeatureChoosers(bufferManager, wkpf);
		this.pipeline = new Pipeline(contextManager, featureChoosers);	
		this.pluginManager = new PluginManager(wkpf, contextManager, pipeline, bufferManager);
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
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Fail to start progression server.");
		}
	}
	
	public void shutdown() {
		this.server.shutdown();
		this.wkpf.shutdown();
	}
	
	public void attachShutDownHook() {
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new Thread() {
			public void run() {
				shutdown();
			}
		});
	}
	
	public static void main(String[] args) {
		PeerInfo peerInfo = new PeerInfo("localhost", 10000);
		ProgressionServer server = new ProgressionServer(peerInfo);
		server.start();
		server.attachShutDownHook();
	}

}
