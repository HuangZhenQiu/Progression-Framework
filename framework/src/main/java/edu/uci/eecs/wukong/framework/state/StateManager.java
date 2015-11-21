package edu.uci.eecs.wukong.framework.state;

import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.framework.manager.PluginManager;
import edu.uci.eecs.wukong.framework.model.StateModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;


/**
 * StateManager is used for persisting states of progression server into local file system.
 * The states include node id, current initialized PrClass, link table and component map. These
 * states will be recovered from file system when restart a progression server.
 * 
 * Later it will be also used for check pointing models generated within progression server.
 *
 */
public class StateManager implements StateUpdateListener {
	private static Logger logger = LoggerFactory.getLogger(StateManager.class);
	private Configuration configuration = Configuration.getInstance();
	private static Gson gson = new Gson();
	private WKPF wkpf;
	private PluginManager pluginManager;
	private String path;
	
	public StateManager(WKPF wkpf, PluginManager pluginManager) {
		try {
			this.wkpf = wkpf;
			this.pluginManager = pluginManager;
			this.wkpf.registerStateListener(this);
			this.pluginManager.register(this);
			this.path = configuration.getStateFilePath();
			logger.info(path);
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function triggered by state update components
	 */
	public void update() {
		persist();
	}
	
	/**
	 * Write current states into local file system to remove effort of 
	 * join MTPN when restart progression server each time.
	 * 
	 */
	public void persist() {
		StateModel model = new StateModel(wkpf, pluginManager);
		String json = gson.toJson(model);
		try {
			FileWriter writer = new FileWriter(path.toString());
			BufferedWriter bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(json);
			bufferedWriter.close();
		} catch (IOException ex) {
			logger.error("Fail to write state information on the path of :" + path);
		}
	}
	
	/**
	 * Read states from local file system, and transform to StateModel.
	 */
	public StateModel recover() {
		String content = readFile(path.toString());
		if (content != null && content != "") {
			StateModel model = gson.fromJson(content, StateModel.class);
			return model;
		}

		return null;
	}
	
	private String readFile(String file) {
		
		StringBuffer buffer = new StringBuffer();
		try {
			String line = null;
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			while((line = bufferedReader.readLine()) != null) {
				buffer.append(line);
			}
			
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			logger.error("Can't find file in the path of :" + path);
		} catch (IOException ex) {
			logger.error("Fail to read state information for the path of :" + path);
		}
		return buffer.toString();
	}
	
	public String getPath() {
		return path.toString();
	}
	
	public static void main(String[] args) {
		//StateManager manager = new StateManager();
	}
}
