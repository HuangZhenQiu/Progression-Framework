package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.Arrays;
import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;
import edu.uci.eecs.wukong.framework.util.OperatorUtil;

/**
 * Compute Detrended Fluctuation Analysis from a time series X and length of boxes L.
 * 
 * The first step to compute DFA is to integrate the signal. Let original series
 * be X = [x(1), x(2), p..., x(N)].
 * 
 * The integrated signal Y = [y(1), y(2), ..., y(N)] is obtained as follows
 * y(k) = \sum_{i=1}^{k}{x(i) - avr} where avr is the mean of X.
 * 
 * The second step is to partition/slice/segment the integrated sequence Y into boxes.
 * At least two boxes are needed for computing DFA. Box sizes are specified by the L
 * argument of this function. By default, It is from 1/5 of signal length to one (x - 5)-th
 * of the signal length, where x is the nearest power of 2 from the length of the signal,
 * i.e., 1/16, 1/32, 1.64, 128.
 * 
 * In each box, a linear least square fitting is employed on data in the box.
 * Denote the series on fitted line as Yn. Its k-th elements, yn(k), corresponds to y(k)
 * 
 * For fitting in each box, there is a residue, the sum of squares of all offset, difference
 * between actual points and points on fitted line.
 * 
 * F(n) denotes the square root of average total residue in all boxes when box length is n,
 * thus total_residue = \sum_{k=1}^{N}{y(k) - yn(k)}
 * F(n) = \sqrt(Total_Residue/N)
 * 
 * The computing to F(n) is carried out for every box length n. Therefore, a relationship
 * between n and F(n) can be obtained. In general, F(n) increases when n increases.
 * 
 * Finally, the relation between F(n) and n is analyzed. A least square fitting is performed
 * between log(F(n)) and log(n). The slop of the fitting line is the DFA value, denoted as Alpha.
 * To white noise, Alpha should be 0.5. Higher level of signal complexity is related to higher Alpha.
 * 
 * 
 * 
 * @author peter
 *
 */
public class DetrendedFluctationAnalysisOperator extends SisoOperator<Short, Double> {

	public DetrendedFluctationAnalysisOperator() {
		super(Short.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		double[] x = OperatorUtil.extractDoubleValues(data);
		double avr = avr(x);
		double[] y = OperatorUtil.cumulativeSum(x);
		OperatorUtil.arrayMinus(y, avr);
		
		int length = ((int)Math.log(x.length)) - 8;
		double[] L = new double[length];
		for (int i = 4; i < 4 + length; i++) {
			L[i-4] = Math.floor(x.length / Math.pow(2, i));
		}
		
		double[] f = new double[length];
		Arrays.fill(f, 0.0);
		
		for (int i = 0; i < x.length; i++) {
			int n = (int)L[i];
			if (n == 0){
				// TODO
			}
			
			for (int j = 0; j  < x.length; j = j + n) {
				if (j + n  < x.length){
					double[][] c = new double[2][n];
					double[] cy = new double[n];
					for (int k = j; k < j + n; k++) {
						c[0][k - j] = k;
						c[1][k -j] = 1;
						cy[k - j] = y[k];
					}
					f[i] += OperatorUtil.leastSquare(c, cy);
				}
			}
			f[i] /= ((x.length / n) * n);
		}
		
		// TODAO (Peter Huang)
		return 0.0;
	}
	
	
	private double avr(double[] x) {
		if (x == null || x.length == 0) {
			return 0;
		}
		
		double sum = 0;
		for (int i = 0; i < x.length; i++) {
			sum += x[i];
		}
		
		return sum/x.length;
	}
}
