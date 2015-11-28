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
	private static final String GATEWAY_IP = "GATEWAY.IP";
	private static final String GATEWAY_PORT = "GATEWAY.PORT";
	private static final String PROGRESSION_SERVER_IP = "PROGRESSION.SERVER.IP";
	private static final String PROGRESSION_SERVER_PORT = "PROGRESSION.SERVER.PORT";
	private static final String STATE_ENABLED = "STATE.ENABLED";
	private static final String STATE_FILE_PATH = "STATE.PATH";
	private static final String MONITOR_ENABLED = "MONITOR.ENABLED";
	private static final String MONITOR_BACKEND = "MONITOR.BACKEND";
	private static final String MONITOR_MONGO_URL = "MONITOR.MONGO.URL";
	private static final String MONITOR_MONGO_DATABASE = "MONITOR.MONGO.DATABASE";
	private static final String MONITOR_MONGO_COLLECTION = "MONITOR.MONGO.COLLECTION";
	private static final String MONITOR_XMPP_TOPIC = "MONITOR.XMPP.TOPIC";
	private static final String HUE_ADDRESS = "HUE.ADDRESS";
	private static final String HUE_POST = "HUE.PORT";
	private static final String DEMO_KICHEN_SECONDS = "DEMO.KICHEN.SECONDS";
	private static final String DEMO_TABLE_SECONDS = "DEMO.TABLE.SECONDS";
	private static final String DEMO_APPLICATION_ID = "DEMO.APPLICATION.ID";
	private static final String DEMO_KICHEN_SLIDER_ID = "DEMO.KICHEN.SLIDER.ID";
	private static final String DEMO_TABLE_SLIDER_ID = "DEMO.TABLE.SLIDER.ID";
	private static final String DEMO_OUTER_SLIDER_ID = "DEMO.OUTER.SLIDER.ID";
	private static final String DEMO_WALL_SLIDER_ID = "DEMO.WALL.SLIDER.ID";
	
	private static Configuration configuration;

	private static Properties properties = new Properties();

	private Configuration() {
		
		try {
			InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(CONFIG_PATH);
			properties.load(inputStream);
		} catch (Exception e) {
			System.out.println("Property File '" + CONFIG_PATH + "' not found in the classpath");
			System.exit(-1);
		}
		
	}
	
	public static synchronized Configuration getInstance() {
		if (configuration == null) {
			configuration = new Configuration();
		}
		return configuration;
	}
	
	public String getProgressionServerIP() {
		return properties.getProperty(PROGRESSION_SERVER_IP);
	}
	
	public Short getProgressionServerPort() {
		return Short.parseShort(properties.getProperty(PROGRESSION_SERVER_PORT));
	}
	
	public String getGatewayIP() {
		return properties.getProperty(GATEWAY_IP);
	}
	
	public boolean isStateEnabled() {
		return getStateEnabled().equals("true");
	}
	
	public String getStateEnabled() {
		return properties.getProperty(STATE_ENABLED, "false");
	}
	
	public String getStateFilePath() {
		return properties.getProperty(STATE_FILE_PATH, "local/state.json");
	}
	
	public boolean isMonitorEnabled() {
		return getMonitorEnabled().equals("true");
	}
	
	public String getMonitorEnabled() {
		return properties.getProperty(MONITOR_ENABLED, "false");
	}
	
	public String getMonitorBackend() {
		return properties.getProperty(MONITOR_BACKEND, "mongoDB");
	}
	
	public String getMonitorXMPPTOPIC() {
		return properties.getProperty(MONITOR_XMPP_TOPIC);
	}
	
	public String getMonitorMongoURL() {
		return properties.getProperty(MONITOR_MONGO_URL);
	}
	
	public String getMonitorMongoDataBase() {
		return properties.getProperty(MONITOR_MONGO_DATABASE);
	}
	
	public String getMonitorMongoCollection() {
		return properties.getProperty(MONITOR_MONGO_COLLECTION);
	}
	
	public Short getGatewayPort() {
		return Short.parseShort(properties.getProperty(GATEWAY_PORT));
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
	
	public String getHueAddress() {
		return properties.getProperty(HUE_ADDRESS);
	}
	
	public String getHuePort() {
		return properties.getProperty(HUE_POST);
	}
	
	public int getDemoKichenSeconds() {
		return Integer.parseInt(properties.getProperty(DEMO_KICHEN_SECONDS));
	}
	
	public int getDemoTableSeconds() {
		return Integer.parseInt(properties.getProperty(DEMO_TABLE_SECONDS));
	}
	
	public String getDemoApplicationId() {
		return properties.getProperty(DEMO_APPLICATION_ID);
	}
	
	public String getKichenSliderId() {
		return properties.getProperty(DEMO_KICHEN_SLIDER_ID);
	}
	
	public String getTableSliderId() {
		return properties.getProperty(DEMO_TABLE_SLIDER_ID);
	}
	
	public String getOuterSliderId() {
		return properties.getProperty(DEMO_OUTER_SLIDER_ID);
	}
	
	public String getWallSliderId() {
		return properties.getProperty(DEMO_WALL_SLIDER_ID);
	}
}
