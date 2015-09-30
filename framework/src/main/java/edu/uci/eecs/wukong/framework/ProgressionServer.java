package edu.uci.eecs.wukong.framework;

import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.manager.ContextManager;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.manager.PluginManager;
import edu.uci.eecs.wukong.framework.pipeline.Pipeline;
import edu.uci.eecs.wukong.plugin.test.TestPlugin;
import edu.uci.eecs.wukong.rpc.netty.CommunicationServer;
import edu.uci.eecs.wukong.rpc.netty.service.DataService;
import edu.uci.eecs.wukong.rpc.netty.service.ProgressionDataServiceFactory;

public class ProgressionServer {
	private static Logger logger = LoggerFactory.getLogger(ProgressionServer.class);
	
	private CommunicationServer server;
	private ContextManager contextManager;
	private BufferManager bufferManager;
	private PluginManager pluginManager;
	private Pipeline pipeline;
	private ConfigurationManager configurationManager;
	
	public ProgressionServer(PeerInfo peerInfo) {
		
		init(peerInfo);
		this.bufferManager = new BufferManager();
		this.contextManager = new ContextManager();
		this.configurationManager = new ConfigurationManager();
		this.pipeline = new Pipeline(contextManager, configurationManager);	
		this.pluginManager = new PluginManager(contextManager, bufferManager, pipeline);
	}
	
	/**
	 *  Read the configuration from config files, build connections with
	 *  all the agents listed and collect the information about virtual machines.
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
			this.pluginManager.init();
			this.server.start();
			this.pipeline.start();
			// this.registerTestPlugin();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Fail to start progression server.");
		}
	}
	
	// Only for testing purpose
	private void registerTestPlugin() {
		TestPlugin plugin = new TestPlugin();
		try {
			pluginManager.bindPlugin(plugin, null);
		} catch (Exception e) {
			logger.info(e.toString());
		}
	}
	
	public void shutdown() {
		
		this.server.shutdown();
	}
	
	
	public static void main(String[] args) {
		PeerInfo peerInfo = new PeerInfo("localhost", 10000);
		ProgressionServer server = new ProgressionServer(peerInfo);
		server.start();
	}

}
