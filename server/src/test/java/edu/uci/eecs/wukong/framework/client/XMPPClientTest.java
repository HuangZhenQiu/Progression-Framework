package edu.uci.eecs.wukong.framework.client;

import edu.uci.eecs.wukong.edge.demo.DemoFactor;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.xmpp.XMPPFactorClient;

public class XMPPClientTest {
	private final static Configuration systemConfig = Configuration.getInstance(); 

	public static void main(String[] args) {
		try {
			byte[] overhad = new byte[10000000]; 
			BaseFactor factor = new DemoFactor(1, 2, 3, 4, 5, 6);
			factor.setTimestamp(System.currentTimeMillis());
			// factor.setPayload(overhad);
			XMPPFactorClient client = new XMPPFactorClient(
					systemConfig.getXMPPTestUserName(), systemConfig.getXMPPPassword());
			client.publish("test", factor);
			Thread.sleep(5000);
		} catch (InterruptedException exception) {
			System.out.println(exception.toString());
		}
	}
}
