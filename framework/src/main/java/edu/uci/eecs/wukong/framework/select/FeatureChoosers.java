package edu.uci.eecs.wukong.framework.select;

import edu.uci.eecs.wukong.framework.extension.impl.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.plugin.Plugin;
import edu.uci.eecs.wukong.framework.model.NPP;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureChoosers {
	private static Logger logger = LoggerFactory.getLogger(FeatureChoosers.class);
	// Network Id of progression server
	private int nid;
	private BufferManager bufferManager;
	private Map<Plugin, FeatureChooser> chooserMap;
	
	public FeatureChoosers(int nid, BufferManager bufferManager) {
		this.nid = nid;
		this.bufferManager = bufferManager;
		this.chooserMap = new HashMap<Plugin, FeatureChooser>();
	}
	
	@SuppressWarnings("rawtypes")
	public void addFeatureExtractionExtenshion(FeatureAbstractionExtension extention) {
		Plugin plugin = extention.getPlugin();
		Map<Operator, Map<NPP, Integer>> bindMap = new HashMap<Operator, Map<NPP, Integer>> ();
		for (Operator operator : extention.registerOperators()) {
			Map<Integer, Integer>  portToInterval = operator.bind();
			Map<NPP, Integer> nppMap = new HashMap<NPP, Integer> ();
			for (Entry<Integer, Integer> entry : portToInterval.entrySet()) {
				NPP npp = new NPP(nid, plugin.getPortId(), entry.getKey().byteValue());
				nppMap.put(npp, entry.getValue());
			}
			bindMap.put(operator, nppMap);
		}
		
		FeatureChooser chooser = new FeatureChooser(bufferManager, bindMap);
		chooserMap.put(plugin, chooser);
	}
	
	public List<Number> choose(Plugin plugin) throws Exception {
		if (chooserMap.get(plugin) == null) {
			logger.error("Fail to choose Feature Chooser for " + plugin.toString());
			throw new Exception("Fail to choose Feature Chooser for " + plugin.toString());
		}
		return chooserMap.get(plugin).choose();
	}
	
	public void clear() {
		chooserMap.clear();
	}
}
