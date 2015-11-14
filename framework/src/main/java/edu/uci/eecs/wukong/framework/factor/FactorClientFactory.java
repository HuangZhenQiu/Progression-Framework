package edu.uci.eecs.wukong.framework.factor;

import edu.uci.eecs.wukong.framework.xmpp.XMPPFactorClient;

/**
 * Factory that initialize the FactorClient with configuration
 */
public class FactorClientFactory {

	public static FactorClient getFactorClient() {
		return XMPPFactorClient.getInstance();
	}
}
