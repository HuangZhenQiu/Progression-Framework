package edu.uci.eecs.wukong.framework.manager;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.client.ConfigurationClient;
import edu.uci.eecs.wukong.framework.entity.ConfigurationReport;
import edu.uci.eecs.wukong.framework.entity.HueEntity;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class ConfigurationManager{
	private final static Configuration configuration = Configuration.getInstance();
	private static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	private Map<String, ConfigurationClient> clients;
	private static Gson gson = new Gson();
	
	public enum ConfigurationType {
		PUT,
		POST
	}
	
	public ConfigurationManager() {
		clients = new HashMap<String, ConfigurationClient>();
		ConfigurationClient masterClient = new ConfigurationClient(
				"Master", configuration.getMasterAddress(), configuration.getMasterPort(), "configuration");
		ConfigurationClient hueClient = new ConfigurationClient(
				"Hue", configuration.getHueAddress(), configuration.getHuePort(), "api/newdeveloper/lights/1/state" /** temporary **/);
		clients.put(masterClient.getName(), masterClient);
		clients.put(hueClient.getName(), hueClient);
	}
	
	public void sendMasterReport(ConfigurationType type,  ConfigurationReport report) {
		try {
			ConfigurationClient client = clients.get("Master");
			client.sendConfigurationMessage(type, gson.toJson(report));
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to master");
			logger.error(e.toString());
		}
	}
	
	public void sendHueConfiguration(ConfigurationType type, HueEntity entity) {
		try {
			ConfigurationClient client = clients.get("Hue");
			client.sendConfigurationMessage(type, gson.toJson(entity.getContent()));
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to Hue");
			logger.error(e.toString());
		}
	}
}