package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SimoOperator;

public class PowerSpectralIntensityOperator extends SimoOperator<Number, Number> {
	private static Logger logger = LoggerFactory.getLogger(PowerSpectralIntensityOperator.class);

	public PowerSpectralIntensityOperator() {
		super(Number.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Number> operate(List<DataPoint<Number>> data) {
		logger.info("PowerSpectralIntensityOperator is executed");
		return null;
	}

}
