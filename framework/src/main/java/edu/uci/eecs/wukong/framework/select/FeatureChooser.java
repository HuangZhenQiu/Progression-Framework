package edu.uci.eecs.wukong.framework.select;

import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;
import edu.uci.eecs.wukong.framework.operator.SimoOperator;
import edu.uci.eecs.wukong.framework.operator.MimoOperator;
import edu.uci.eecs.wukong.framework.operator.MisoOperator;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.model.NPP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.Number;

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
	private Map<Operator<?>, Map<NPP, Integer>> operaters;
	
	public FeatureChooser(PipelinePrClass prClass, BufferManager bufferManager, Map<Operator<?>, Map<NPP, Integer>>  operaters) {
		this.bufferManager = bufferManager;
		this.operaters = operaters;
		this.prClass = prClass;
	}
	
	public List<Number> choose() {
		List<Number> result = new ArrayList<Number> ();
		for (Entry<Operator<?>, Map<NPP, Integer>> entry : operaters.entrySet()) {
			List<List<DataPoint<Short>>> dataList = new ArrayList<List<DataPoint<Short>>>();
			for (Entry<NPP, Integer> nppEntry: entry.getValue().entrySet()) {
				List<DataPoint<Short>> data = bufferManager.getData(nppEntry.getKey(), nppEntry.getValue());
				dataList.add(data);
			}
			
			if (entry.getKey() instanceof SisoOperator) {
				SisoOperator<Short> singleOperator = (SisoOperator<Short>) entry.getKey();
				result.add(singleOperator.operate(dataList.get(0)));
			} else if (entry.getKey() instanceof MisoOperator ) {
				MisoOperator<Short> multipleOperator = (MisoOperator<Short>) entry.getKey();
				result.add(multipleOperator.operate(dataList));
			} else if (entry.getKey() instanceof MimoOperator) {
				MimoOperator<Short> mimoOperator = (MimoOperator<Short>) entry.getKey();
				result.addAll(mimoOperator.operate(dataList));
			} else if (entry.getKey() instanceof SimoOperator) {
				SimoOperator<Short> simoOperator = (SimoOperator<Short>) entry.getKey();
				result.addAll(simoOperator.operate(dataList.get(0)));
			}
		}
		
		logger.info("Choosed features for PrClass : " + prClass);
		return result;
	}
}
