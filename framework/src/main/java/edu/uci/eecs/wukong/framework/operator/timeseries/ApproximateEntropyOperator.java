package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;

public class ApproximateEntropyOperator extends SisoOperator<Short, Double> {

	public ApproximateEntropyOperator() {
		super(Short.class);
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
