package edu.uci.eecs.wukong.framework.prclass;

import edu.uci.eecs.wukong.framework.reconfig.ConfigurationManager;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.framework.util.Configuration;

public abstract class AgentPrClass extends SystemPrClass {
	private static Configuration configuration = Configuration.getInstance();
	protected ConfigurationManager configManager;
	
	public AgentPrClass(String name, WKPF wkpf, PrClassMetrics metrics) {
		super(name, wkpf, metrics);
		this.configManager =  ConfigurationManager.getInstance();
	}

	public void remap() {
		configManager.remapping(this.appId);
	}
}
