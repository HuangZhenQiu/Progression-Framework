package edu.uci.eecs.wukong.framework.select;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.operator.SingleOperator;
import edu.uci.eecs.wukong.framework.operator.MultipleOperator;
import edu.uci.eecs.wukong.framework.model.NPP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.lang.Number;

/*
 * Feature chooser instance of a plugin.
 * 
 */
public class FeatureChooser {
	private BufferManager bufferManager;
	private Map<Operator<?>, Map<NPP, Integer>> operaters;
	
	public FeatureChooser(BufferManager bufferManager, Map<Operator<?>, Map<NPP, Integer>>  operaters) {
		this.bufferManager = bufferManager;
		this.operaters = operaters;
	}
	
	public List<Number> choose() {
		List<Number> result = new ArrayList<Number> ();
		for (Entry<Operator<?>, Map<NPP, Integer>> entry : operaters.entrySet()) {
			List<List<DataPoint<Short>>> dataList = new ArrayList<List<DataPoint<Short>>>();
			for (Entry<NPP, Integer> nppEntry: entry.getValue().entrySet()) {
				List<DataPoint<Short>> data = bufferManager.getData(nppEntry.getKey(), nppEntry.getValue());
				dataList.add(data);
			}
			if (dataList.size() == 1) {
				SingleOperator singleOperator = (SingleOperator) entry.getKey();
				result.add(singleOperator.operate(dataList.get(0)));
			} else {
				MultipleOperator multipleOperator = (MultipleOperator) entry.getKey();
				result.add(multipleOperator.operate(dataList));
			}
		}
		
		return result;
	}
}
