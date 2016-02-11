package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public abstract class MisoOperator<T> extends AbstractOperator<T> {
	
	protected MisoOperator(Class<T> type) {
		super(type);
	}
	
	public abstract T operate(List<List<DataPoint<T>>> data);
}
