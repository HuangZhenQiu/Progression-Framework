package edu.uci.eecs.wukong.framework.monitor;

import java.util.Timer;
import java.util.TimerTask;

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
	private MonitoringTask currentTask;
	
	public static class MonitoringTask extends TimerTask {
		private MonitorService service;
		public MonitoringTask(MonitorService service) {
			this.service = service;
		}
		
		@Override
		public void run() {
			service.bulkPush();
		}
	}
	
	public MonitorManager(WKPF wkpf) {
		try { 
			this.service = MonitorServiceFactory.createMonitorService();
			this.timer = new Timer();
			this.currentPeriod = BUFFER_SEND_INTERVAL;
			this.currentTask = new MonitoringTask(service);
			wkpf.registerMonitorListener(this);

		} catch (ServiceInitilizationException e) {
			logger.error("Fail to initialize monitor manager: " + e.toString());
		}
	}

	
	public void start() {
		timer.schedule(currentTask, 0, BUFFER_SEND_INTERVAL);
	}
	
	public void stop() {
		currentTask.cancel();
	}
	
	public void updatePeriod(long period) {
		currentTask.cancel();
		currentTask = new MonitoringTask(service);
		this.timer.schedule(currentTask, 0, period);
		this.currentPeriod = period;
	}

	@Override
	public void onMonitorMessage(MonitorDataModel model) {
		service.send(model);
	}
	
	public void close() {
		stop();
		timer.cancel();
	}
	
	public long getCurrentPeriod() {
		return this.currentPeriod;
	}
}
