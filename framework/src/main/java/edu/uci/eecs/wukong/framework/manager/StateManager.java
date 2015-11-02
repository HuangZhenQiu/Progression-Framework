package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.framework.manager.PluginManager;
import edu.uci.eecs.wukong.framework.model.StateModel;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
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
	private static Gson gson = new Gson();
	private WKPF wkpf;
	private PluginManager pluginManager;
	private URI path;
	
	@VisibleForTesting
	public StateManager() {
		try {
			this.path = new URI(System.getProperty("user.dir") + "/local/test.json");
			System.out.println(path);
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
		StateModel model = new StateModel(wkpf, pluginManager);
		String json = gson.toJson(model);
	}
	
	public void recover() {
		//TODO (Peter Huang)
	}
	
	private String readFile(String file) throws IOException {
		
	}
	
	public String getPath() {
		return path.toString();
	}
	
	public static void main(String[] args) {
		StateManager manager = new StateManager();
		System.out.println(System.getProperty("user.dir") + "/local/test.json");
	}
}
