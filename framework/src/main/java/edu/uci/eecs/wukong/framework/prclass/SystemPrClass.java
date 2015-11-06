package edu.uci.eecs.wukong.framework.prclass;

import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;

public abstract class SystemPrClass extends PrClass {
	private ConfigurationManager configManager;
	
	public SystemPrClass(String name) {
		super(name);
		configManager =  ConfigurationManager.getInstance();
	}
	
	public ConfigurationManager getConfigurationManager() {
		return configManager;
	}
}
