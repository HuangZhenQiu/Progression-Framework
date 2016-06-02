package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public class DefaultOperator extends SisoOperator<Short, Short> {

	public DefaultOperator() {
		super(Short.class);
	}

	@Override
	public Short operate(List<DataPoint<Short>> data) throws Exception {
		if (data.size() !=0) {
			return data.get(0).getValue();
		}
		
		return null;
	}

}