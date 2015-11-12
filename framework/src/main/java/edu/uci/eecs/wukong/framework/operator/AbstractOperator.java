package edu.uci.eecs.wukong.framework.operator;

import java.util.Map;
import java.util.HashMap;

public abstract class AbstractOperator<T> implements Operator<T> {
	private Map<Integer, Integer> sourceKeys;
	
	public AbstractOperator() {
		sourceKeys = new HashMap<Integer, Integer>();
	}
	
	public void addDataSource(Integer key, int interval) {
		this.sourceKeys.put(key, interval);
	}
	
	public Map<Integer, Integer> bind() {
		return sourceKeys;
	}
}
