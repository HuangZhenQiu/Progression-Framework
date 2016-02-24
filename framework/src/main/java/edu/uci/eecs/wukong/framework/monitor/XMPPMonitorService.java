package edu.uci.eecs.wukong.framework.monitor;

import edu.uci.eecs.wukong.framework.model.MonitorDataModel;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class XMPPMonitorService extends MonitorService {
	private static Configuration configuration = Configuration.getInstance();
	private static XMPPMonitorService service;
	
	private XMPPMonitorService() {
		
	}
	
	public static synchronized XMPPMonitorService getInstance() {
		if (service == null) {
			service = new XMPPMonitorService();
		}
		
		return service;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(MonitorDataModel model) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

}
