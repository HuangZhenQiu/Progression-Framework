package edu.uci.eecs.wukong.framework.util;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;

import java.util.List;

public class OperatorUtil {
	private static StandardDeviation std = new StandardDeviation();
	private static SimpleRegression regression = new SimpleRegression();
	
	/**
	 * Determine whether one vector is the range of another vector
	 * 
	 * The two vectors should have equal length.
	 * 
	 * 
	 * @param template one of two lists being compared
	 * @param scroll one of two lists being compared
	 * @param d two list match if their distance is less than d
	 * @return
	 */
	public static boolean inRange(List<Float> template, List<Float> scroll, double d) {
		if (template == null || scroll == null || template.size() != scroll.size()) {
			return false;
		}
		
		for (int i =0; i< template.size(); i++) {
			if (Math.abs(template.get(i) - scroll.get(i)) > d) {
				return false;
			}
		}
		
		return true;
	}
	
	public static double[] extractDoubleValues(List<DataPoint<Short>> data) {
		double[] result = new double[data.size()];
		
		for (int i = 0; i < data.size(); i++) {
			result[i] = data.get(i).getValue();
		}
		
		return result;
	}
	
	public static double sum(List<Double> data) {
		double sum = 0;
		for (int i = 0; i < data.size(); i++) {
			sum += data.get(i);
		}
		
		return sum;
	}
	
	/**
	 * Building a set of embedding sequences from given time series X with lag Tau
	 * and embedding dimension DE. Let X = [x(1), x(2), ... , X(N)], then for each
	 * i such that 1 < i < N - (D - 1) * Tau, we build an embedding sequence,
	 * Y(i) = [x(i), x(i + Tau), ..., x(i + (D -1) * Tau)]. All embedding sequence
	 * are placed in a matrix Y.
	 * 
	 * 
	 * 
	 * @param x	A time series array
	 * @param tau the lag or delay when building embedding sequence
	 * @param dimension the embedding dimension
	 * @return a 2-D list embedding matrix built
	 */
	public static double[][] buildEmbedSequence(double[] x,  int tau, int dimension) {
		int n = x.length;
		// Can not build such a matrics, because D * Tau > N
		assert(dimension * tau < n);
		
		//  Tau has to be at least 1
		assert(tau >= 1);
		double[][]  y = new double[n - (dimension -1) * tau][dimension];
		for (int i = 0; i < n - (dimension - 1) * tau; i++) {
			for (int j = 0; j < dimension; j++) {
				y[i][j] = x[i + j * tau];
			}
		}
		
		return y;
		
	}
	
	/**
	 * Compute the Petrosian Fractal Dimension of a time series from either two
	 * cases below:
	 * 
	 * 1. X, the time series of type list (default)
	 * 2. D, the first order differential sequence of X
	 * 
	 * 
	 * @param data
	 * @param diff
	 * @return
	 */
	public static double pfd(double[] data, double[] diff) {
		if (diff == null) {
			diff = firstOrderDiff(data);
		}
		
		// Number of sign changes in derivative of the signal
		int nDelta = 0;
		for (int i = 1; i < data.length; i++) {
			if (diff[i] * diff[i - 1] < 0) {
				nDelta += 1;
			}
		}
		
		int n = data.length;
		return Math.log10(n) / (Math.log10(n) + Math.log10(n /n + 0.4 * nDelta));
	}
	
	/**
	 * Compute the Hurst exponent of X. If the output H = 0.4, the behavior
	 * of the time-series is similar to random walk. If H < 0.5, the time-series
	 * cover less "distance" than a random walk, vice verse.
	 * 
	 * @param x a time series list
	 * @return double Hurst exponent
	 */
	public static double hurst(double[] x) {
		int n = x.length;
		double[] t = new double[n];
		for (int i = 0; i < n; i++) {
			t[i] = i + 1;
		}
		
		double[] y = cumulativeSum(x);
		double[] avr = new double[n];
		
		for (int i = 0; i< n; i++) {
			avr[i] = y[i]/t[i];
		}
		
		double[] st = new double[n];
		double[] rt = new double[n];
		
		for (int i = 0 ; i < n; i++) {
			st[i] = std.evaluate(x, i + 1, n - (i + 1));
			double[] xt = arrayMinus(y, arrayMultiply(t, avr[i]));
			rt[i] = max(xt, 0, i + 1) - min(xt, 0, i + 1);
		}
		
		double[] rs = arrayDivide(rt, st);
		rs = arrayLog(rs);
		
		// remember to clean it before using it.
		regression.clear();
		for (int i = 1; i < rs.length; i++) {
			regression.addData(rs[i], rs[i]);
		}
		
		return regression.getSlope();
	}
	
	public static double leastSquare(double[][] x, double[] y) {
		regression.clear();
		for (int i = 0; i < x.length; i++) {
			regression.addObservation(x[i], y[i]);
		}
		
		return regression.getSlope();
	}
	
	public static double[] cumulativeSum(double[] data) {
		assert(data != null &&  data.length > 0);
		double[] sum = new double[data.length];
		sum[0] = data[0];
		for (int i = 1; i < data.length; i++) {
			sum[i] = sum[i - 1] + data[i];
		}
		
		return sum;
	}
	
	
	public static double mean(double[] a) {
		assert(a != null);
		assert(a.length >= 1);
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i];
		}
		
		return sum / a.length;
	}
	
	public static double min(double[] a, int start, int end) {
		assert(a != null);
		assert(start >= 0 && start < a.length);
		assert(end >= 0 && end < a.length);
		assert(start <= end);
		
		double min = a[start];
		for (int i = start + 1; i <= end; i++) {
			if (min > a[i]) {
				min = a[i];
			}
		}
		
		return min;
	}
	
	public static double max(double[] a, int start, int end) {
		assert(a != null);
		assert(start >= 0 && start < a.length);
		assert(end >= 0 && end < a.length);
		assert(start <= end);
		
		double max = a[start];
		for (int i = start + 1; i <= end; i++) {
			if (max < a[i]) {
				max = a[i];
			}
		}
		
		return max;
	}
	
	public static double[] arrayMultiply(double[] a, double[] b) {
		assert(a != null);
		assert(b != null);
		assert(a.length == b.length);
		
		double[] result =  new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] * b[i];
		}
		
		return result;
	}
	
	public static double[] arrayMultiply(double[] a, double b) {
		assert(a != null);
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] * b;
		}
		
		return result;
	}
	
	public static double[] arrayMinus(double[] a, double[] b) {
		assert(a != null);
		assert(b != null);
		assert(a.length == b.length);
		
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] - b[i];
		}
		
		return result;
	}
	
	public static double[] arrayMinus(double[] a, double b) {
		assert(a != null);
		
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] - b;
		}
		
		return result;
	}
	
	public static double[] firstOrderDiff(double[] a) {
		assert(a != null && a.length >= 2);
		
		double[] result = new double[a.length - 1];
		for (int i = 1; i < a.length; i++) {
			result[i - 1] = a[i] - a[i - 1];
		}
		
		return result;
	}
	
	public static double sum2(double[] data) {
		double sum = 0;
		for (int i = 0; i < data.length; i++) {
			sum += Math.pow(data[i], 2.0);
		}
		
		return sum;
	}
	
	public static double  mean2(double[] data) {
		return sum2(data) / data.length;
	}
	
	public static double[] arrayDivide(double[] a, double[] b) {
		assert(a != null);
		assert(b != null);
		assert(a.length == b.length);
		
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] / b[i];
		}
		
		return result;
	}
	
	public static double[] arrayLog(double[] a) {
		assert(a != null && a.length > 0);
		
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = Math.log(a[i]);
		}
		
		return result;
	}
}
