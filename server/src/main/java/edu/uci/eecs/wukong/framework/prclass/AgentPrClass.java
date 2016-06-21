package edu.uci.eecs.wukong.framework.prclass;

import edu.uci.eecs.wukong.framework.reconfig.ConfigurationManager;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.framework.util.Configuration;

import java.util.List;

import edu.uci.eecs.wukong.framework.predict.Predict;

public abstract class AgentPrClass extends SystemPrClass {
	private static Configuration configuration = Configuration.getInstance();
	
	public AgentPrClass(String name, Poller poller, PrClassMetrics metrics) {
		super(name, poller, metrics);
		this.configManager =  ConfigurationManager.getInstance();
	}

	public void remap(List<Predict> predicts) {
		configManager.remapping(this.appId, predicts);
	}
}
