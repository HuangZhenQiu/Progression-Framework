package edu.uci.eecs.wukong.framework.reconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.entity.ConfigurationReport;
import edu.uci.eecs.wukong.framework.entity.HueEntity;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorClient;
import edu.uci.eecs.wukong.framework.factor.FactorClientFactory;
import edu.uci.eecs.wukong.framework.predict.Predict;
import edu.uci.eecs.wukong.framework.service.ConfigurationService;
import edu.uci.eecs.wukong.framework.service.MappingService;
import edu.uci.eecs.wukong.framework.util.Configuration;


/**
 * Configuration Manager is designed for delegate PrClass to talk with master. It is composed of several services
 * that are designed for exchange information for reconfigure or re-mappiung an application.
 */
public class ConfigurationManager{
	private final static Configuration configuration = Configuration.getInstance();
	private static Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
	private static ConfigurationManager manager;
	private static Gson gson = new Gson();
	private ConfigurationService masterConfig;
	private ConfigurationService HueService;
	private MappingService mappingService;
	private FactorClient factorClient;
	
	public enum ConfigurationType {
		PUT,
		POST
	}
	
	private ConfigurationManager() {
		this.masterConfig = new ConfigurationService(
				"Master", configuration.getMasterAddress(), configuration.getMasterPort(), "configuration");
		this.HueService = new ConfigurationService(
				"Hue", configuration.getHueAddress(), configuration.getHuePort(), "api/newdeveloper/lights/1/state" /** temporary **/);
		this.mappingService = new  MappingService(
				"Map", configuration.getMasterAddress(), configuration.getMasterPort(), "applications");
		this.factorClient = FactorClientFactory.getFactorClient();
	}
	
	public synchronized static ConfigurationManager getInstance() {
		if (manager == null) {
			manager = new ConfigurationManager();
		}
		
		return manager;
	}
	
	public void publish(String topic, BaseFactor factor) {
		factorClient.publish(topic, factor);
	}
	
	public void remapping(String appId, List<Predict> predicts) {
		try {
			mappingService.sendMappingMessage(appId, predicts);
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to master");
			logger.error(e.toString());
		}
	}
	
	public void sendMasterReport(ConfigurationType type,  ConfigurationReport report) {
		try {
			masterConfig.sendConfigurationMessage(type, gson.toJson(report));
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to master");
			logger.error(e.toString());
		}
	}
	
	public void sendHueConfiguration(ConfigurationType type, HueEntity entity) {
		try {
			HueService.sendConfigurationMessage(type, gson.toJson(entity.getContent()));
		} catch (Exception e) {
			logger.error("Fail to send reconfiguration message to Hue");
			logger.error(e.toString());
		}
	}
}