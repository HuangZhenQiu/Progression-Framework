package edu.uci.eecs.wukong.framework.monitor;

import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.util.Constants;
import edu.uci.eecs.wukong.framework.exception.ServiceInitilizationException;

public class MonitorServiceFactory {
	public static Configuration configuration = Configuration.getInstance();
	
	public static MonitorService createMonitorService() throws ServiceInitilizationException {
		try {
			if (configuration.getMonitorBackend().equals(Constants.Monitor.XMPP)) {
				return XMPPMonitorService.getInstance();
			} else if (configuration.getMonitorBackend().equals(Constants.Monitor.MONGODB)) {
				return MongoDBMonitorService.getInstance();
			} else {
				throw new ServiceInitilizationException("Unrecogized backend configuration: " + configuration.getMonitorBackend());
			}
		} catch (Exception e) {
			throw new ServiceInitilizationException(e.getMessage());
		}
	}
}
