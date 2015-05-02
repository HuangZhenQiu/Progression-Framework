package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public abstract class SingleOperator<T extends Number> extends AbstractOperator<Number> {

	/**
	 * @param data
	 * 
	 * @return
	 */
	public abstract T operate(List<DataPoint<T>> data);
}
