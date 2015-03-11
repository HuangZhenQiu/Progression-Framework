package edu.uci.eecs.wukong.framework.operator;

import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;

/**
 * It is the interface for a progression plugin to define a feature extractor.
 * 
 * @author Peter
 *
 */
public interface Operator<T> {
	
	/**
	 * The bind function should return the logical keys for the data sources, from which progression
	 * framework grasp data in the time range from n time units to most recent. 
	 * 
	 * @return the map of logical key and the time units for the key.
	 */
	public Map<LogicalKey, Integer> bind();
	
	
	/**
	 * @param data
	 * 
	 * @return
	 */
	public T operate(List<List<DataPoint<T>>> data);
	
	
	public void addDataSource(LogicalKey key, int interval);
}
