package edu.uci.eecs.wukong.framework.operator;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

public abstract class SimoOperator<T extends Number> extends AbstractOperator<Number> {
	private static Logger logger = LoggerFactory.getLogger(SimoOperator.class);
	
	@Override
	public void addDataSource(Integer key, int interval) {
		if (sourceKeys.isEmpty()) {
			sourceKeys.put(key, interval);
		} else {
			logger.error("Can only add one property into the operator");
		}
	}
	
	/**
	 * @param data
	 * 
	 * @return
	 */
	public abstract List<T> operate(List<DataPoint<T>> data);
}