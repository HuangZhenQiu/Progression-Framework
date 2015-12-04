package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

/**
 * This Operator use the pir data in buffer to determine a place's 
 * occupancy.
 *
 */
public class ExistenceOperator extends SingleOperator<Byte> {

	@Override
	public Byte operate(List<DataPoint<Byte>> data) {
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getValue() == 1) {
				return (byte)1;
			}
		}
		
		return (byte) 0;
	}
}
