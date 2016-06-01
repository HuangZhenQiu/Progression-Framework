package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;

public class HurstExpoentOperator extends SisoOperator<Short, Double> {

	public HurstExpoentOperator() {
		super(Short.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
