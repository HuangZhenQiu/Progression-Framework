package edu.uci.eecs.wukong.framework.manager;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.client.ConfigurationClient;
import edu.uci.eecs.wukong.framework.entity.ConfigurationEntity;
import edu.uci.eecs.wukong.framework.entity.ConfigurationReport;

public class ConfigurationManager{
	private static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	private ConfigurationClient client;
	
	public ConfigurationManager() {
		client = ConfigurationClient.getInstance();
	}
	
	public void send(String appId, List<ConfigurationEntity> entities) {
		try {
			if (entities!=null && entities.size() > 0) {
				client.sendConfigurationMessage(new ConfigurationReport(appId, entities));
			}
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to master");
			logger.error(e.toString());
		}
	}
	
	public void send(String appId, ConfigurationEntity entity) {
		try {
			client.sendConfigurationMessage(new ConfigurationReport(appId, entity));
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to master");
			logger.error(e.toString());
		}
	}
}