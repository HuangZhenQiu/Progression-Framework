package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.List;
import java.lang.Math;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;
import edu.uci.eecs.wukong.framework.util.OperatorUtil;


/**
 * Compute Hjorth mobility of a time series
 *
 * @author peter
 *
 */
public class HjorthMobilityOperator extends SisoOperator<Short, Double> {

	public HjorthMobilityOperator() {
		super(Short.class);
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		double[] x = OperatorUtil.extractDoubleValues(data);
		double[] diff = OperatorUtil.firstOrderDiff(x);
		double[] d = new double[x.length];
		d[0] = x[0];
		for (int i = 0; i < diff.length; i++) {
			d[i] = diff[i];
		}
		
		int n = data.size();
		double m2 = OperatorUtil.mean2(d);
		double tp = OperatorUtil.sum2(x);
		
		return Math.sqrt(m2 / tp);
	}
}
