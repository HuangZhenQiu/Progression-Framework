package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.framework.manager.PluginManager;

/**
 * StateManager is used for persisting states of progression server into local file system.
 * The states include node id, current initialized PrClass, link table and component map. These
 * states will be recovered from file system when restart a progression server.
 * 
 * Later it will be also used for check pointing models generated within progression server.
 *
 */
public class StateManager {
	private WKPF wkpf;
	private PluginManager pluginManager;
	
	public StateManager(WKPF wkpf, PluginManager pluginManager) {
		this.wkpf = wkpf;
		this.pluginManager = pluginManager;
	}
	
	/**
	 * 
	 * 
	 */
	public void persist() {
		//TODO (Peter Huang)
	}
	
	public void recover() {
		//TODO (Peter Huang)
	}
}
