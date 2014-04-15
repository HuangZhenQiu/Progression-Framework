package edu.uci.eecs.wukong.network.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.google.protobuf.BlockingService;
import com.google.protobuf.Service;

import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClient;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;

import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.RpcTimeoutExecutor;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutChecker;
import com.googlecode.protobuf.pro.duplex.timeout.TimeoutExecutor;

public class CommunicationClient {
	
	private PeerInfo server;
	private PeerInfo client;
	private boolean secure;
	private boolean compress;
	private boolean isAgent;
	
	private RpcServerCallExecutor executor;
	private DuplexTcpClientPipelineFactory clientFactory;
	private RpcTimeoutExecutor timeoutExecutor;
	private RpcTimeoutChecker timeoutChecker;
	private CleanShutdownHandler shutdownHandler;
	private RpcConnectionEventNotifier rpcEventNotifier;
	private RpcConnectionEventListener rpcEventListener;
	
	private Bootstrap bootstrap;
	
	public CommunicationClient(PeerInfo server, PeerInfo client, boolean secure, boolean compress, boolean isAgent) {
		
		this.server = server;
		this.client = client;
		this.secure = secure;
		this.compress = compress;
		this.isAgent = isAgent;
		
		this.executor = new ThreadPoolCallExecutor(3, 200);
		this.clientFactory = new DuplexTcpClientPipelineFactory();
		this.timeoutExecutor = new TimeoutExecutor(1, 5);
		this.timeoutChecker= new TimeoutChecker();
		this.shutdownHandler = new CleanShutdownHandler();
		this.rpcEventNotifier = new RpcConnectionEventNotifier();
		
		if(isAgent) {
			this.rpcEventListener = new AgentRpcConnectionEventListener();
		} else {
			this.rpcEventListener = new ProgressionRpcConnectionEventListener();
		}
		
		this.bootstrap = new Bootstrap();
		
		init();
		
	}
	
	private void init() {
		
		this.clientFactory.setConnectResponseTimeoutMillis(10000);
		this.clientFactory.setRpcServerCallExecutor(executor);
		this.clientFactory.setCompression(compress);
		
		if(secure) {
			//TODO: huangzhenqiu0825 add the SSL configure here.
		}
		
		timeoutChecker.setTimeoutExecutor(timeoutExecutor);
		timeoutChecker.startChecking(clientFactory.getRpcClientRegistry());
		shutdownHandler.addResource(executor);
		shutdownHandler.addResource(timeoutChecker);
		shutdownHandler.addResource(timeoutExecutor);
		rpcEventNotifier.setEventListener(rpcEventListener);
		
		clientFactory.registerConnectionEventListener(rpcEventNotifier);
		
	}
	
	/**
	 * Register the unblocking service into server.
	 * 
	 * @param allowTimeout
	 * @param service
	 */
	public void registerService(boolean allowTimeout, Service service) {
		this.clientFactory.getRpcServiceRegistry().registerService(allowTimeout, service);
	}
	
	/**
	 * Register the blocking service into server.
	 * 
	 * @param allowTimeout
	 * @param service
	 */
	public void registerBlockingService(boolean allowTimeout, BlockingService service) {
		this.clientFactory.getRpcServiceRegistry().registerBlockingService(allowTimeout, service);
	}
	
	/**
	 * This function builds connection between client and server. The return value RpcClient
	 * should be put into your implementation of service stub. Therefore, the service client
	 * can use the channel to send out request.
	 * 
	 * @return RpcClient
	 * @throws Exception
	 */
	public RpcClient start() throws Exception {
		
		this.bootstrap.group(new NioEventLoopGroup());
		this.bootstrap.handler(clientFactory);
		this.bootstrap.channel(NioSocketChannel.class);
		this.bootstrap.option(ChannelOption.TCP_NODELAY, false);
		this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
		this.bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
		this.bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);
		
		return clientFactory.peerWith(this.server, bootstrap);
		
		
	}

}
