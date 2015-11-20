package edu.uci.eecs.wukong.framework.monitor;

import edu.uci.eecs.wukong.framework.model.MonitorDataModel;

public interface MonitorListener {

	public void onMonitorMessage(MonitorDataModel model);
}
