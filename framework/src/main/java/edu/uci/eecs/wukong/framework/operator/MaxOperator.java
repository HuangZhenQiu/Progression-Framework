package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class MaxOperator<T extends Number> extends SisoOperator<Number> {

	@Override
	public Number operate(List<DataPoint<Number>> data) {
		// TODO Auto-generated method stub
		Number max = Double.MIN_VALUE;
		for (DataPoint<Number> point : data) {
			if (max.doubleValue() < point.getValue().doubleValue()) {
				max = point.getValue().doubleValue();
			}
		}
		return max;
	}
}
