package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;
import edu.uci.eecs.wukong.framework.util.OperatorUtil;

/**
 * Compute the Hjorth Fractal Dimension of a time series X, kmax is 
 * an HFD parameter
 * 
 * 
 * @author peter
 *
 */
public class HiguchiFractalDimensionOperator extends SisoOperator<Short, Double> {
	private static int DEFAULT_KMAX = 100;
	private int kmax;

	public HiguchiFractalDimensionOperator(Integer kmax) {
		super(Short.class);
		if (kmax == null) {
			this.kmax = kmax;
		} else {
			kmax = DEFAULT_KMAX;
		}
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		int n = data.size();
		double[] l = new double[kmax - 1];
		double[][] x = new double[kmax - 1][2];
		for (int k = 1; k < kmax; k ++) {
			double[] lk = new double[k];
			for (int m = 0; m < k; m++) {
				double lmk = 0d;
				for (int i = 1; i < Math.floor((n - m) / k); i++) {
					lmk  += Math.abs(data.get(m + i * k).getValue() - data.get( m + i * k -k).getValue());
				}
				lmk = lmk * (n - 1) / Math.floor((n - m) / new Float(k)) / k;
				lk[m] =lmk;
			}
			
			l[k] = Math.log(OperatorUtil.mean(lk));
			x[k][0] = Math.log(new Float(1) / k);
			x[k][1] = 1;
 		}
		
		// Need to confirm with f
		return OperatorUtil.leastSquare(x, l);
	}

}
