package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public abstract class MultipleOperator<T extends Number> extends AbstractOperator<Number> {
	
	public abstract Number operate(List<List<DataPoint<T>>> data);
}
