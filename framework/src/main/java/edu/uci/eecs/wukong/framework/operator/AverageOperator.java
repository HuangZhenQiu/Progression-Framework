package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class AverageOperator extends SisoOperator<Short, Double> {

	public AverageOperator() {
		super(Short.class);
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		if (data.size() == 0) {
			return 0.0d;
		}

		Double avr = new Double(0);
		for (DataPoint<Short> point : data) {
			avr += point.getValue().doubleValue();
		}

		return avr;
	}
}