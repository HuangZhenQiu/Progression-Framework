package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class DefaultOperator<T> extends SingleOperator<T> {

	@Override
	public T operate(List<DataPoint<T>> data) {
		if (data.size() !=0) {
			return data.get(0).getValue();
		}
		return null;
	}

}