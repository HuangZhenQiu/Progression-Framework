package edu.uci.eecs.wukong.framework.monitor;

import edu.uci.eecs.wukong.framework.model.MonitorDataModel;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class MongoDBMonitorService implements MonitorService {
	private static Configuration configuration = Configuration.getInstance();
	private static MongoDBMonitorService service;
	
	private MongoDBMonitorService() {
		
	}
	
	public static synchronized MongoDBMonitorService getInstance() {
		if (service == null) {
			service = new MongoDBMonitorService();
		}
		
		return service;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(MonitorDataModel model) {
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
