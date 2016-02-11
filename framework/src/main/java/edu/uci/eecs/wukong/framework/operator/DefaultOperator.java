package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class DefaultOperator<T extends Number> extends SisoOperator<T> {

	public DefaultOperator(Class<T> type) {
		super(type);
	}

	@Override
	public T operate(List<DataPoint<T>> data) throws Exception {
		if (data.size() !=0) {
			return data.get(0).getValue();
		}
		
		return null;
	}

}