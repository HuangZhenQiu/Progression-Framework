package edu.uci.eecs.wukong.framework.operator;

import java.util.Map;

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
	 * @return the map of propertyId and the time units for the key.
	 */
	public Map<Integer, Integer> bind();
	
	
	public void addDataSource(Integer key, int interval);
}
