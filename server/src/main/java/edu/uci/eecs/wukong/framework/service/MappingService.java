package edu.uci.eecs.wukong.framework.service;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappingService extends AbstractHttpService{
	private final static Logger LOGGER = LoggerFactory.getLogger(MappingService.class);
	
	public MappingService(String name, String ip, String port, String method) {
		// Use default connection parameters setting, 20 connections 2 routs per
		// connection.
		super(name, ip, port, method);
	}
	
	public void sendMappingMessage(String appId) {
		send(new HttpPost(CONFIG_URL), appId);
	}
}
