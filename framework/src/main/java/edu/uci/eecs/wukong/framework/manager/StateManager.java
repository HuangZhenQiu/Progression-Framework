package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;
import edu.uci.eecs.wukong.framework.manager.PluginManager;
import edu.uci.eecs.wukong.framework.model.StateModel;

import java.net.URI;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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
	private Configuration configuration = Configuration.getInstance();
	private static Gson gson = new Gson();
	private WKPF wkpf;
	private PluginManager pluginManager;
	private URI path;
	
	@VisibleForTesting
	public StateManager(String path) {
		try {
			this.path = new URI(System.getProperty("user.dir") + File.pathSeparator + path);
			System.out.println(path);
		} catch (Exception e) {
			logger.info("Can't file with path: " + System.getProperty("user.dir") + File.pathSeparator + path);
		}
	}
	
	public StateManager(WKPF wkpf, PluginManager pluginManager) {
		String fileName = null;
		try {
			this.wkpf = wkpf;
			this.pluginManager = pluginManager;
			fileName = System.getProperty("user.dir") + File.pathSeparator + configuration.getStateFilePath();
			this.path = new URI(fileName);
		} catch (Exception e) {
			logger.info("Can't file with path: " + fileName);
			File file = new File(fileName);
			
			try {
				file.createNewFile();
			} catch (IOException err) {
				logger.info("Fail to create state file with path: " + fileName);
				System.exit(1);
			}
			
			logger.info("Created new state file with path: " + fileName);
		}
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
			logger.error("Can't state file in the path of :" + path);
		} catch (IOException ex) {
			logger.error("Fail to read state information for the path of :" + path);
		}
		return buffer.toString();
	}
	
	public String getPath() {
		return path.toString();
	}
	
	public static void main(String[] args) {
		StateManager manager = new StateManager("local/test.json");
	}
}
