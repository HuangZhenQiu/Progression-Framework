package edu.uci.eecs.wukong.framework.util;

import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private static final String CONFIG_PATH = "config.properties";
	private static final String XMPP_SERVER_NAME  = "XMPP.SERVER.NAME";
	private static final String XMPP_ADDRESS = "XMPP.ADDRESS";
	private static final String XMPP_PORT = "XMPP.PORT";
	private static final String XMPP_USERNAME = "XMPP.USERNAME";
	private static final String XMPP_PASSWORD = "XMPP.PASSWORD";
	private static final String MASTER_ADDRESS = "MASTER.ADDRESS";
	private static final String MASTER_PORT = "MASTER.PORT";
	private static Configuration configuration;
	private static Properties properties = new Properties();

	private Configuration() {
		
		try {
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_PATH);
			properties.load(inputStream);
		} catch (Exception e) {
			System.out.println("Property File '" + CONFIG_PATH + "' not found in the classpath");
		}
		
	}
	
	public static synchronized Configuration getInstance() {
		if (configuration == null) {
			configuration = new Configuration();
		}
		return configuration;
	}
	
	public String getXMPPServerName() {
		return properties.getProperty(XMPP_SERVER_NAME);
	}
	
	public String getXMPPAddress() {
		return properties.getProperty(XMPP_ADDRESS);
	}
	
	public String getXMPPPort() {
		return properties.getProperty(XMPP_PORT);
	}
	
	public String getXMPPUserName() {
		return properties.getProperty(XMPP_USERNAME);
	}
	
	public String getXMPPPassword() {
		return properties.getProperty(XMPP_PASSWORD);
	}
	
	public String getMasterAddress() {
		return properties.getProperty(MASTER_ADDRESS);
	}
	
	public String getMasterPort() {
		return properties.getProperty(MASTER_PORT);
	}
}
