package edu.uci.eecs.wukong.framework.operator.basic;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;

public class SumOperator extends SisoOperator<Short, Double> {
	
	public SumOperator() {
		super(Short.class);
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) {
		// TODO Auto-generated method stub
		Double number = 0d;
		for (DataPoint<Short> point : data) {
			number = number.doubleValue() + point.getValue().doubleValue();
		}
		
		return number;
	}
}
