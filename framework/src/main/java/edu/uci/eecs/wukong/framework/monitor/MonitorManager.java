package edu.uci.eecs.wukong.framework.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.exception.ServiceInitilizationException;
import edu.uci.eecs.wukong.framework.model.MonitorDataModel;
import edu.uci.eecs.wukong.framework.monitor.MonitorService;
import edu.uci.eecs.wukong.framework.monitor.MonitorServiceFactory;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

public class MonitorManager implements MonitorListener {
	private static Logger logger = LoggerFactory.getLogger(MonitorManager.class);
	private MonitorService service;
	
	public MonitorManager(WKPF wkpf) {
		try { 
			service = MonitorServiceFactory.createMonitorService();
			wkpf.registerMonitorListener(this);
			service.init();
		} catch (ServiceInitilizationException e) {
			logger.error("Fail to initialize monitor manager: " + e.toString());
		}
	}

	@Override
	public void onMonitorMessage(MonitorDataModel model) {
		service.send(model);
	}
	
	public void close() {
		service.close();
	}
}
