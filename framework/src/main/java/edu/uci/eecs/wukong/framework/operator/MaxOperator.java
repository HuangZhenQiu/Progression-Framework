package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class MaxOperator<T extends Number> extends SisoOperator<T> {

	public MaxOperator(Class<T> type) {
		super(type);
	}

	@Override
	public T operate(List<DataPoint<T>> data) throws Exception {
		// TODO Auto-generated method stub
		Number max = Double.MIN_VALUE;
		for (DataPoint<T> point : data) {
			if (max.doubleValue() < point.getValue().doubleValue()) {
				max = point.getValue().doubleValue();
			}
		}
		
		return type.getConstructor(double.class).newInstance(max);
	}
}
