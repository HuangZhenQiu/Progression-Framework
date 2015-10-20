package edu.uci.eecs.wukong.framework.manager;

import java.io.File;

import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.framework.manager.PluginManager;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
/**
 * StateManager is used for persisting states of progression server into local file system.
 * The states include node id, current initialized PrClass, link table and component map. These
 * states will be recovered from file system when restart a progression server.
 * 
 * Later it will be also used for check pointing models generated within progression server.
 *
 */
public class StateManager {
	private static Logger logger = LoggerFactory.getLogger(StateManager.class);
	private WKPF wkpf;
	private PluginManager pluginManager;
	private URI path;
	
	@VisibleForTesting
	public StateManager() {
		try {
			this.path = getClass().getResource("local/test.json").toURI();
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
	
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
	
	public String getPath() {
		return path.toString();
	}
	
	public static void main(String[] args) {
		StateManager manager = new StateManager();
		System.out.println(System.getProperty("user.dir") + "/local/test.json");
	}
}
