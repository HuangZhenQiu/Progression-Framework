package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public abstract class MimoOperator<T, E> extends AbstractOperator<T> {
	
	protected MimoOperator(Class<T> type) {
		super(type);
	}

	public abstract List<E> operate(List<List<DataPoint<T>>> data);

}
