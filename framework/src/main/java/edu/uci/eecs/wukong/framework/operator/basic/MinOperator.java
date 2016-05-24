package edu.uci.eecs.wukong.framework.operator.basic;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;

public class MinOperator<T extends Number> extends SisoOperator<T> {

	public MinOperator(Class<T> type) {
		super(type);
	}
	
	@Override
	public T operate(List<DataPoint<T>> data) throws Exception {
		// TODO Auto-generated method stub
		Number min = Double.MAX_VALUE;
		for (DataPoint<T> point : data) {
			if (min.doubleValue() > point.getValue().doubleValue()) {
				min = point.getValue().doubleValue();
			}
		}

		return type.getConstructor(double.class).newInstance(min);
	}
}
