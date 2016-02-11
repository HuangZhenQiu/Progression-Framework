package edu.uci.eecs.wukong.framework.operator;

import java.util.Map;
import java.util.HashMap;

public abstract class AbstractOperator<T> implements Operator<T> {
	protected Map<Integer, Integer> sourceKeys;
	protected Class<T> type;
	
	public AbstractOperator(Class<T> type) {
		this.sourceKeys = new HashMap<Integer, Integer>();
		this.type = type;
	}
	
	public void addDataSource(Integer key, int interval) {
		this.sourceKeys.put(key, interval);
	}
	
	public Map<Integer, Integer> bind() {
		return sourceKeys;
	}
	
	public Class<T> getType() {
		return this.type;
	}
}
