package edu.uci.eecs.wukong.framework.test;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;

import edu.uci.eecs.wukong.framework.nio.NIOUdpServer;
import edu.uci.eecs.wukong.framework.wkpf.MPTNMessageListener;

/**
 * Simulate the function of gateway to assign network Id to tart progression server which is being tested.
 * The the meaning time receive the reply message, and calculate the metrics of server.
 */
public class MockGateway implements MPTNMessageListener {
	private NIOUdpServer server;
	private PerformanceCollector collector;
	private MockReprogrammer programmer;
	private Map<LoadGenerator<?>, Long> generators;
	private Timer timer;
	private WKPFMessageSender sender; // init after server is added into network
	
	public MockGateway(int port, MockReprogrammer programmer, PerformanceCollector collector) {
		this.server = new NIOUdpServer(port);
		this.generators = new HashMap<LoadGenerator<?>, Long> ();
		this.server.addMPTNMessageListener(this);
		this.timer = new Timer();
		this.collector = collector;
		this.programmer = programmer;
	}

	@Override
	public void onMessage(ByteBuffer bytes) {
		// TODO Auto-generated method stub
		
	}
	
	public void schedule(LoadGenerator<?> generator, long period) {
		generator.setSender(sender);
		generators.put(generator, period);
	}
	
	public void generateLoad() {
		for (Entry<LoadGenerator<?>, Long> entry : generators.entrySet()) {
			timer.schedule(entry.getKey(), entry.getValue());
		}
	}
	
	public void start() {
		Thread serverThread = new Thread(server);
		serverThread.start();
	}
	
	public void shutdown() {
		server.shutdown();
	}
}
