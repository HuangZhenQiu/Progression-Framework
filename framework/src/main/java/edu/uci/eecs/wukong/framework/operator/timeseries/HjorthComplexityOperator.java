package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;
import edu.uci.eecs.wukong.framework.util.OperatorUtil;

public class HjorthComplexityOperator extends SisoOperator<Short, Double> {

	public HjorthComplexityOperator() {
		super(Short.class);
		// TODO Auto-generated constructor stub
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
		
		double m4 = 0;
		for (int i = 1; i < d.length; i++) {
			m4 += Math.pow(d[i] - d[i - 1], 2.0);
		}
		
		m4 = m4 / n;
		
		return Math.sqrt(m4 * tp / m2 / m2);
	}

}
