package edu.uci.eecs.wukong.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.checkpoint.CheckPointManager;
import edu.uci.eecs.wukong.framework.monitor.MonitorManager;
import edu.uci.eecs.wukong.framework.util.Configuration;

/**
 * A collection of system components used to do dynamic adjustment of parameters on the fly.
 * 
 * @author peter
 *
 */
public class SystemStates {
	private static Logger logger = LoggerFactory.getLogger(SystemStates.class);
	private static boolean monitorEnabled;
	private static boolean checkpointEnabled;
	private static MonitorManager monitorManager;
	private static CheckPointManager checkpointManager;
	private static SystemStates instance;
	
	static  {
		Configuration configuration = Configuration.getInstance();
		monitorEnabled = configuration.isMonitorEnabled();
	}
	
	public static synchronized SystemStates getInstance() {
		if (instance != null) {
			return instance;
		}
		
		return new SystemStates();
	}
	
	protected SystemStates() {
		instance = this;
	}
	
	protected SystemStates(MonitorManager monitorM, CheckPointManager checkpointM) {
		monitorManager = monitorM;
		checkpointManager = checkpointM;
		if (instance == null) {
			instance = this;
		}
	}
	
	public void updateMonitoringState(boolean enabled) {
		if (monitorManager!=null && monitorEnabled != enabled) {
			monitorEnabled = enabled;
			if (enabled) {
				monitorManager.start();
			} else {
				monitorManager.close();
			}
			logger.info("Monitoring state is updated to " + enabled);
		}
	}
	
	public void updateCheckpointState(boolean enabled) {
		if (checkpointManager != null) {
			checkpointEnabled = enabled;
			logger.info("Checkppint state is updated to " + enabled);
		}
	}
	
	/**
	 * Update global monitoring period
	 * 
	 * @param period time period in milliseconds
	 */
	public void updateMonitorInterval(long period) {
		if (monitorManager != null && monitorEnabled) {
			monitorManager.updatePeriod(period);
			logger.info("Monitoring interval is updated to " + period + " millisecond");
		}
	}
	
	/**
	 * Update global checkpoint period
	 * 
	 * @param period time period in milliseconds
	 */
	public void updateCheckpointInterval(long period) {
		if (checkpointManager != null) {
			logger.info("Checkpoint interval is updated to " + period + " millisecond");
		}
	}
	
	public long getCurrentMonitorPeriod() {
		if (monitorManager != null) {
			return monitorManager.getCurrentPeriod();
		} else {
			return 0;
		}
	}
	
	public long getCheckpointPeriod() {
		if (checkpointManager != null) {
			return checkpointManager.getCurrentPeriod();
		} else {
			return 0;
		}
	}
}
