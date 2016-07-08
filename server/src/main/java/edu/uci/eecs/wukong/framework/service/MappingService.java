package edu.uci.eecs.wukong.framework.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.predict.Predict;

public class MappingService extends AbstractHttpService{
	private final static Logger LOGGER = LoggerFactory.getLogger(MappingService.class);
	private final static String METHOD = "remap";
	
	public MappingService(String name, String ip, String port, String method) {
		// Use default connection parameters setting, 20 connections 2 routs per
		// connection.
		super(name, ip, port, method);
	}
	
	public void sendMappingMessage(String appId, List<Predict> predicts) {
		Map<String, Object> parameters = new HashMap<String, Object> ();
		parameters.put("predicts", predicts);
		send(new HttpPost(CONFIG_URL + "/" + appId + "/" + METHOD), parameters);
	}
}
