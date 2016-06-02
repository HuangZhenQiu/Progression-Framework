package edu.uci.eecs.wukong.framework.monitor;

import edu.uci.eecs.wukong.framework.model.MonitorDataModel;

/**
 * Interface for monitoring service. Progression Server support forward monitoring data
 * into mongoDB, and xmpp;
 */
public abstract class MonitorService {
	
	public abstract void send(MonitorDataModel model);
	
	public abstract void close();
	
	public abstract void bulkPush();
}
