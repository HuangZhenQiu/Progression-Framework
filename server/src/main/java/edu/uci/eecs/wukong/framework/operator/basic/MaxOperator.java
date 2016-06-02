package edu.uci.eecs.wukong.framework.operator.basic;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;

public class MaxOperator extends SisoOperator<Short, Double> {

	public MaxOperator() {
		super(Short.class);
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		// TODO Auto-generated method stub
		Double max = Double.MIN_VALUE;
		for (DataPoint<Short> point : data) {
			if (max.doubleValue() < point.getValue().doubleValue()) {
				max = point.getValue().doubleValue();
			}
		}
		
		return max;
	}
}
