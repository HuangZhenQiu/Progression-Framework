package edu.uci.eecs.wukong.framework.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.client.ConfigurationClient;
import edu.uci.eecs.wukong.framework.entity.ConfigurationEntity;

public class ConfigurationManager{
	private static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	private ConfigurationClient client = ConfigurationClient.getInstance();
	
	public ConfigurationManager() {
		
	}
	
	public void send(List<ConfigurationEntity> entities) {
		try {
			client.sendConfigurationMessage(entities);
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to master");
			logger.error(e.toString());
		}
	}
}