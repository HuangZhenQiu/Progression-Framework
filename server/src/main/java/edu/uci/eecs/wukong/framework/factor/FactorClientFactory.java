package edu.uci.eecs.wukong.framework.factor;

import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.xmpp.XMPPFactorClient;

/**
 * Factory that initialize the FactorClient with configuration
 */
public class FactorClientFactory {
	private final static Configuration systemConfig = Configuration.getInstance(); 
	public static FactorClient client;
	public static synchronized FactorClient getFactorClient() {
		if (client == null) {
			client = new XMPPFactorClient(systemConfig.getXMPPUserName(), systemConfig.getXMPPPassword());
			return client;
		} else {
			return client;
		}
	}
}
