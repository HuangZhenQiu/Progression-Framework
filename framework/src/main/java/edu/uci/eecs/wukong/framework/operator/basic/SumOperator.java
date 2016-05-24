package edu.uci.eecs.wukong.framework.operator.basic;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;

public class SumOperator<T extends Number> extends SisoOperator<Number> {
	
	public SumOperator() {
		super(Number.class);
	}

	@Override
	public Number operate(List<DataPoint<Number>> data) {
		// TODO Auto-generated method stub
		Number number = 0;
		for (DataPoint<Number> point : data) {
			number = number.doubleValue() + point.getValue().doubleValue();
		}
		
		return number;
	}
}
