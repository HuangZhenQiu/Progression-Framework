package edu.uci.eecs.wukong.framework.manager;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.entity.ConfigurationReport;
import edu.uci.eecs.wukong.framework.entity.HueEntity;
import edu.uci.eecs.wukong.framework.service.ConfigurationService;
import edu.uci.eecs.wukong.framework.util.Configuration;


/**
 * Configuration Manager is designed for delegate PrClass to talk with master. It is composed of several servies
 * that are designed for exchange information for reconfigure or re-mappiung an application.
 * 
 *
 */
public class ConfigurationManager{
	private final static Configuration configuration = Configuration.getInstance();
	private static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	private Map<String, ConfigurationService> clients;
	private static ConfigurationManager manager;
	private static Gson gson = new Gson();
	
	public enum ConfigurationType {
		PUT,
		POST
	}
	
	private ConfigurationManager() {
		clients = new HashMap<String, ConfigurationService>();
		ConfigurationService masterClient = new ConfigurationService(
				"Master", configuration.getMasterAddress(), configuration.getMasterPort(), "configuration");
		ConfigurationService hueClient = new ConfigurationService(
				"Hue", configuration.getHueAddress(), configuration.getHuePort(), "api/newdeveloper/lights/1/state" /** temporary **/);
		clients.put(masterClient.getName(), masterClient);
		clients.put(hueClient.getName(), hueClient);
	}
	
	public synchronized static ConfigurationManager getInstance() {
		if (manager == null) {
			manager = new ConfigurationManager();
		}
		
		return manager;
	}
	
	public void sendMasterReport(ConfigurationType type,  ConfigurationReport report) {
		try {
			ConfigurationService client = clients.get("Master");
			client.sendConfigurationMessage(type, gson.toJson(report));
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to master");
			logger.error(e.toString());
		}
	}
	
	public void sendHueConfiguration(ConfigurationType type, HueEntity entity) {
		try {
			ConfigurationService client = clients.get("Hue");
			client.sendConfigurationMessage(type, gson.toJson(entity.getContent()));
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to Hue");
			logger.error(e.toString());
		}
	}
}