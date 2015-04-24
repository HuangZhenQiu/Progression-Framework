package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public abstract class MultipleOperator<T> extends AbstractOperator<T> {
	
	public abstract T operate(List<List<DataPoint<T>>> data);
}
