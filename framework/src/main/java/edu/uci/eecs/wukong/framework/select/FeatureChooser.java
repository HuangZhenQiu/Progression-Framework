package edu.uci.eecs.wukong.framework.select;

import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;
import edu.uci.eecs.wukong.framework.operator.SimoOperator;
import edu.uci.eecs.wukong.framework.operator.AbstractOperator;
import edu.uci.eecs.wukong.framework.operator.MimoOperator;
import edu.uci.eecs.wukong.framework.operator.MisoOperator;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.model.NPP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Feature chooser instance of a plugin.
 * 
 */
public class FeatureChooser {
	private static Logger logger = LoggerFactory.getLogger(FeatureChooser.class);
	private PipelinePrClass prClass;
	private BufferManager bufferManager;
	private Map<AbstractOperator<?>, Map<NPP, Integer>> operaters;
	
	public FeatureChooser(PipelinePrClass prClass, BufferManager bufferManager, Map<AbstractOperator<?>, Map<NPP, Integer>>  operaters) {
		this.bufferManager = bufferManager;
		this.operaters = operaters;
		this.prClass = prClass;
	}
	
	public List<Object> choose() {
		List<Object> result = new ArrayList<Object> ();
		for (Entry<AbstractOperator<?>, Map<NPP, Integer>> entry : operaters.entrySet()) {
			List<List<DataPoint<Object>>> dataList = new ArrayList<List<DataPoint<Object>>>();
			for (Entry<NPP, Integer> nppEntry: entry.getValue().entrySet()) {
				List<DataPoint<Object>> data = bufferManager.getData(nppEntry.getKey(), nppEntry.getValue());
				dataList.add(data);
			}
			
			if (!dataList.isEmpty()) {
				try {
					if (entry.getKey() instanceof SisoOperator) {
						SisoOperator<Object> singleOperator = (SisoOperator<Object>) entry.getKey();
						result.add(singleOperator.operate(dataList.get(0)));
					} else if (entry.getKey() instanceof MisoOperator ) {
						MisoOperator<Object, Object> multipleOperator = (MisoOperator<Object, Object>) entry.getKey();
						result.add(multipleOperator.operate(dataList));
					} else if (entry.getKey() instanceof MimoOperator) {
						MimoOperator<Object, Object> mimoOperator = (MimoOperator<Object, Object>) entry.getKey();
						result.addAll(mimoOperator.operate(dataList));
					} else if (entry.getKey() instanceof SimoOperator) {
						SimoOperator<Object, Object> simoOperator = (SimoOperator<Object, Object>) entry.getKey();
						result.addAll(simoOperator.operate(dataList.get(0)));
					}
				} catch (Exception e) {
					logger.error("Fail to use operator: " + entry.getKey() + " to fetch data.");
				}
			}
		}
		
		logger.info("Choosed features for PrClass : " + prClass);
		return result;
	}
}
