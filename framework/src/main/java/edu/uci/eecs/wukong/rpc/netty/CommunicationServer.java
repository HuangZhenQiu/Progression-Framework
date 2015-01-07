package edu.uci.eecs.wukong.rpc.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerPipelineFactory;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;

import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutExecutor;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutExecutor;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It is a TCP based protocol buffer enabled communication server. It is based on netty and deplex.
 * We wrapper the deplex server to make the server-side more configurable.
 * 
 * To use it, you need to define some protocol buffer based the services, and add them into the sever.
 * In the end, you just need to start the server. The server will handle the multiple thread processing
 * for you.
 * 
 * @author peterhuang
 *
 */

public class CommunicationServer {
	
	private static Logger logger = LoggerFactory.getLogger(CommunicationServer.class);
	
	private PeerInfo peerInfo;
	private RpcServerCallExecutor executor;
	private DuplexTcpServerPipelineFactory pipeLineFactory;
	private RpcTimeoutExecutor timeOutExecutor;
	private RpcTimeoutChecker timeOutChecker;
	private RpcConnectionEventNotifier rpcEventNotifier;
	private RpcConnectionEventListener rpcEventListener;
	
	private ServerBootstrap bootstrap;
	
	private boolean secure;
	private boolean nodelay;
	
	public CommunicationServer(PeerInfo peerInfo, boolean secure, boolean nodelay, boolean isAgent) {
		
		this.secure = secure;
		this.nodelay = nodelay;
		this.peerInfo = peerInfo;
		
		//parameters should come from configuration file
		this.executor = new ThreadPoolCallExecutor(3, 200);
		this.pipeLineFactory = new DuplexTcpServerPipelineFactory(peerInfo);
		this.timeOutExecutor = new TimeoutExecutor(1, 5);
		this.timeOutChecker = new TimeoutChecker();
		this.rpcEventNotifier = new RpcConnectionEventNotifier();
		
		if(isAgent) {
			this.rpcEventListener = new AgentRpcConnectionEventListener();
		} else {
			this.rpcEventListener = new ProgressionRpcConnectionEventListener();
		}
		
		this.bootstrap = new ServerBootstrap();
		init();
		
	}
	
	
	private void init() {
		this.pipeLineFactory.setRpcServerCallExecutor(this.executor);
		if(this.secure) {
			//TODO huangzhenqiu0825 add SSL facility
		}
		this.timeOutChecker.setTimeoutExecutor(timeOutExecutor);
		this.timeOutChecker.startChecking(pipeLineFactory.getRpcClientRegistry());
		
		this.rpcEventNotifier.setEventListener(rpcEventListener);
		this.pipeLineFactory.registerConnectionEventListener(rpcEventNotifier);
		
	}
	
	/**
	 * Register the unblocking service into server.
	 * 
	 * @param allowTimeout
	 * @param service
	 */
	public void registerService(boolean allowTimeout, Service service) {
		this.pipeLineFactory.getRpcServiceRegistry().registerService(allowTimeout, service);
	}
	
	/**
	 * Register the blocking service into server.
	 * 
	 * @param allowTimeout
	 * @param service
	 */
	public void registerBlockingService(boolean allowTimeout, BlockingService service) {
		this.pipeLineFactory.getRpcServiceRegistry().registerBlockingService(allowTimeout, service);
	}
	
	
	/**
	 * Start the deplex communication server, with predefined buffer size;
	 * 
	 * TODO: huangzhenqiu0825 replace hardcoded paramters with values from configuration
	 * 
	 */
	public void start() {

		this.bootstrap.group(
				new NioEventLoopGroup(0, new RenamingThreadFactoryProxy("boss",
						Executors.defaultThreadFactory())),
				new NioEventLoopGroup(0, new RenamingThreadFactoryProxy(
						"worker", Executors.defaultThreadFactory())));
		
		bootstrap.channel(NioServerSocketChannel.class);
		bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
		bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);
		bootstrap.childOption(ChannelOption.SO_RCVBUF, 1048576);
		bootstrap.childOption(ChannelOption.SO_SNDBUF, 1048576);
		bootstrap.option(ChannelOption.TCP_NODELAY, nodelay);
		bootstrap.childHandler(pipeLineFactory);
		bootstrap.localAddress(peerInfo.getPort());

		// Bind and start to accept incoming connections.
		CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
		//shutdownHandler.setBootstraps(bootstrap.);
		shutdownHandler.addResource(executor);
		
		shutdownHandler.addResource(timeOutChecker);
		shutdownHandler.addResource(timeOutExecutor);

	    this.bootstrap.bind();
	  
	    logger.info("Communication Sever Started at:" + peerInfo);
		
	}
	
	public void shutdown() {
		
		//this.bootstrap.shutdown();
	}
	
	public PeerInfo getPeerInfo() {
		
		return this.peerInfo;
	}

	public RpcServerCallExecutor getExecutor() {
		
		return this.executor;
	}
	

}
