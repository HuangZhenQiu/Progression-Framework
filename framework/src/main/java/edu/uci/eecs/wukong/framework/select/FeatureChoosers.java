package edu.uci.eecs.wukong.framework.select;

import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.wkpf.WKPF;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureChoosers {
	private static Logger logger = LoggerFactory.getLogger(FeatureChoosers.class);
	// Network Id of progression server
	private BufferManager bufferManager;
	private WKPF wkpf;
	private Map<PipelinePrClass, FeatureChooser> chooserMap;
	
	public FeatureChoosers(BufferManager bufferManager, WKPF wkpf) {
		this.bufferManager = bufferManager;
		this.wkpf = wkpf;
		this.chooserMap = new HashMap<PipelinePrClass, FeatureChooser>();
	}
	
	@SuppressWarnings("rawtypes")
	public void addFeatureExtractionExtenshion(FeatureExtractionExtension extention) {
		PipelinePrClass plugin = extention.getPrClass();
		Map<Operator<?>, Map<NPP, Integer>> bindMap = new HashMap<Operator<?>, Map<NPP, Integer>> ();
		for (Object object : extention.registerOperators()) {
			Operator operator = (Operator) object;
			Map<Integer, Integer>  portToInterval = operator.bind();
			Map<NPP, Integer> nppMap = new HashMap<NPP, Integer> ();
			for (Entry<Integer, Integer> entry : portToInterval.entrySet()) {
				NPP npp = new NPP(wkpf.getNetworkId(), plugin.getPortId(), entry.getKey().byteValue());
				nppMap.put(npp, entry.getValue());
			}
			bindMap.put(operator, nppMap);
		}
		
		FeatureChooser chooser = new FeatureChooser(extention.getPrClass(), bufferManager, bindMap);
		chooserMap.put(plugin, chooser);
		logger.info("Add Feature Extraction Extension in Feature Choosers for PrClass " + plugin);
	}
	
	public List<Number> choose(PipelinePrClass plugin) throws Exception {
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
