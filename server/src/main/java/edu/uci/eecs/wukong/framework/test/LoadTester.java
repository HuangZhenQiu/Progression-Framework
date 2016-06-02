package edu.uci.eecs.wukong.framework.test;

/**
 * Load test is mainly to generate dynamic workload for a connected progression server. It is mainly to
 * evaluate the response time of each PrClass runs in the server.
 *
 */
public class LoadTester {
	private PerformanceCollector collector;
	private MockGateway gateway;
	private MockReprogrammer programmer;
	
	public LoadTester(int port) {
		this.programmer = new MockReprogrammer();
		this.collector = new PerformanceCollector();
		this.gateway = new MockGateway(port, programmer, collector);
	}
	
	public void start() {
		gateway.start();
	}
	
	public static void main(String args[]) {
		LoadTester tester = new LoadTester(5775);
		tester.start();
	}
}
