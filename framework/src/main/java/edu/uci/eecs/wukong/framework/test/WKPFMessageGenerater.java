package edu.uci.eecs.wukong.framework.test;

import edu.uci.eecs.wukong.framework.nio.NIOUdpClient;
import java.io.IOException;

/***
 * A wrapper of NIOUDPClient. It provides some functions to generate 
 * WKPF messages as input to have integration test with progression server.
 *
 */
public class WKPFMessageGenerater {
	private NIOUdpClient client;
	
	public WKPFMessageGenerater(String domain, int port) throws IOException {
		client = new NIOUdpClient(domain, port);
	}
	
	public void sendWriteProperty() {
		
	}
	
	
	public static void main(String[] arg) {
		
	}
}
