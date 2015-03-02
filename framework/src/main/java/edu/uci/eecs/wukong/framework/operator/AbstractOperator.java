package edu.uci.eecs.wukong.framework.operator;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;
import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public abstract class AbstractOperator implements Operator {
	private Map<LogicalKey, Integer> sourceKeys;
	
	public AbstractOperator() {
		sourceKeys = new HashMap<LogicalKey, Integer>();
	}
	
	public Map<LogicalKey, Integer> bind() {
		return sourceKeys;
	}

	public abstract Object operate(List<List<DataPoint>> data);
}
