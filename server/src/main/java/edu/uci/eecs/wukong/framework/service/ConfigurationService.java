package edu.uci.eecs.wukong.framework.service;

import edu.uci.eecs.wukong.framework.reconfig.ConfigurationManager.ConfigurationType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

public class ConfigurationService extends AbstractHttpService {
	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);


	public ConfigurationService(String name, String ip, String port, String method) {
		// Use default connection parameters setting, 20 connections 2 routs per
		// connection.
		super(name, ip, port, method);
	}
	
	public void sendTestMessage()
			throws ClientProtocolException, IOException {
		sendConfigurationMessage(ConfigurationType.POST, "TEST MESSAGE");
	}
	
	public void sendConfigurationMessage(ConfigurationType type, Object content)
			throws ClientProtocolException, IOException {
		LOGGER.info("Send out configuration report: " + content);
		Map<String, Object> parameters = new HashMap<String, Object> ();
		parameters.put("content", content);
		switch (type) {
			case POST:
				send(new HttpPost(CONFIG_URL), parameters);
			case PUT:
				send(new HttpPost(CONFIG_URL), parameters);
				
		}
		
	}
}
