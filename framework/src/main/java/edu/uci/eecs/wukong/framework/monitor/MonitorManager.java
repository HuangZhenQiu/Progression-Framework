package edu.uci.eecs.wukong.framework.monitor;

import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.exception.ServiceInitilizationException;
import edu.uci.eecs.wukong.framework.model.MonitorDataModel;
import edu.uci.eecs.wukong.framework.monitor.MonitorService;
import edu.uci.eecs.wukong.framework.monitor.MonitorServiceFactory;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

public class MonitorManager implements MonitorListener {
	private static Logger logger = LoggerFactory.getLogger(MonitorManager.class);
	private static long BUFFER_SEND_INTERVAL = 1000 * 30;
	private Timer timer;
	private long currentPeriod;
	private MonitorService service;
	
	public MonitorManager(WKPF wkpf) {
		try { 
			this.service = MonitorServiceFactory.createMonitorService();
			this.timer = new Timer();
			this.currentPeriod = BUFFER_SEND_INTERVAL;
			wkpf.registerMonitorListener(this);
		} catch (ServiceInitilizationException e) {
			logger.error("Fail to initialize monitor manager: " + e.toString());
		}
	}
	
	public void start() {
		timer.schedule(service, 0, BUFFER_SEND_INTERVAL);
	}
	
	public void stop() {
		timer.cancel();
	}
	
	public void updatePeriod(long period) {
		this.timer.cancel();
		this.timer.schedule(service, 0, period);
		this.currentPeriod = period;
	}

	@Override
	public void onMonitorMessage(MonitorDataModel model) {
		service.send(model);
	}
	
	public void close() {
		timer.cancel();
		service.close();
	}
	
	public long getCurrentPeriod() {
		return this.currentPeriod;
	}
}
