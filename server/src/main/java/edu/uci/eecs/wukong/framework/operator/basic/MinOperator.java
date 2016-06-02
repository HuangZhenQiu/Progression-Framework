package edu.uci.eecs.wukong.framework.operator.basic;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;

public class MinOperator extends SisoOperator<Short, Double> {

	public MinOperator() {
		super(Short.class);
	}
	
	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		// TODO Auto-generated method stub
		Double min = Double.MAX_VALUE;
		for (DataPoint<Short> point : data) {
			if (min.doubleValue() > point.getValue().doubleValue()) {
				min = point.getValue().doubleValue();
			}
		}

		return min;
	}
}
