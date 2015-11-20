package edu.uci.eecs.wukong.framework.monitor;

import edu.uci.eecs.wukong.framework.model.MonitorDataModel;

/**
 * Interface for monitoring service. Progression Server support forward monitoring data
 * into mongoDB, and xmpp;
 */
public interface MonitorService {

	public void init();
	
	public void send(MonitorDataModel model);
	
	public void close();
}
