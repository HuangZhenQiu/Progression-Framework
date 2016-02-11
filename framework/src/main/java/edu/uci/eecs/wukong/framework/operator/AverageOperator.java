package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class AverageOperator<T extends Number> extends SisoOperator<T> {

	public AverageOperator(Class<T> type) {
		super(type);
	}

	@Override
	public T operate(List<DataPoint<T>> data) throws Exception {
		if (data.size() == 0) {
			return type.getConstructor(double.class).newInstance(0.0);
		}

		Double avr = new Double(0);
		for (DataPoint<T> point : data) {
			avr += point.getValue().doubleValue();
		}

		return type.getConstructor(double.class).newInstance(avr/data.size());
	}
}