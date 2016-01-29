package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class AverageOperator<T extends Number> extends SisoOperator<Number> {

	@Override
	public Number operate(List<DataPoint<Number>> data) {
		if (data.size() == 0) {
			return new Double(0);
		}

		Double avr = new Double(0);
		for (DataPoint<Number> point : data) {
			avr += point.getValue().doubleValue();
		}

		return avr/data.size();
	}

}