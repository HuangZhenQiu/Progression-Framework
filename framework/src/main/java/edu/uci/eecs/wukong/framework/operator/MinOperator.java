package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class MinOperator<T extends Number> extends SisoOperator<Number> {

	@Override
	public Number operate(List<DataPoint<Number>> data) {
		// TODO Auto-generated method stub
		Number min = Double.MAX_VALUE;
		for (DataPoint<Number> point : data) {
			if (min.doubleValue() > point.getValue().doubleValue()) {
				min = point.getValue().doubleValue();
			}
		}
		return min;
	}
}
