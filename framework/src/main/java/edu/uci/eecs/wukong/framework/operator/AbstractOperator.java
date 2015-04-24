package edu.uci.eecs.wukong.framework.operator;

import java.util.Map;
import java.util.HashMap;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;

public abstract class AbstractOperator<T> implements Operator<T> {
	private Map<LogicalKey, Integer> sourceKeys;
	
	public AbstractOperator() {
		sourceKeys = new HashMap<LogicalKey, Integer>();
	}
	
	public void addDataSource(LogicalKey key, int interval) {
		this.sourceKeys.put(key, interval);
	}
	
	public Map<LogicalKey, Integer> bind() {
		return sourceKeys;
	}
}
