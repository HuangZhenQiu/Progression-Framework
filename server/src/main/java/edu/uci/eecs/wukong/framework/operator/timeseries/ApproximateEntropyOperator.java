package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;

/**
 * Compute approximate entropy of series X, specified by M and R.
 * 
 * Suppose given time series is X = [x(1), x(2), ..., x(N)]. We first build
 * embedding matrix Em, of dimension (N - M + 1), such that the i-th row of Em
 * is x(i), x(i + 1), ... x(i + M -1). Hence, the embedding lag and dimension are
 * 1 and M - 1 respectively. Such a matrix can be built by calling pyeeg function
 * as Em = embed_seq(X, 1, M). Then we build matrix Emp, whose only difference with Em
 * is that the length of each embedding sequence is M + 1
 * 
 * 
 * Denote the i-th and j-th row of Em as Em[i] and Em[j]. Their k-th elements are Em[i][k]
 * and Em[j][k] respectively. The distance between Em[i] and Em[j] is defined as
 * 1) the maximum difference of their corresponding scalar components, thus, max(Em[i] - Em[j], or
 * 2) Euclidean distance. We say two 1-D vector Em[i] and Em[j] *match* in *tolerance* R,
 * if the distance between them is no greater than R, thus, max(Em[i] - Em[j]) <= R.
 * Mostly, the value of R is defined as 20% - 30% of standard deviation of X.
 * 
 * Pick Em[i] as a template, for all j such that 0 < j < N - M + 1, we can check whether 
 * Em[j] matches with Em[i]. Denote the number of Em[j], which is in the range of Em[i],
 * as k[i], which is the i-th element of the vector k. The probability that a random row
 * in Em matches Em[i] is \sigma_1^{N - M + 1} K[i] / (N - M + 1), thus sum(k) / (N - M + 1),
 * denoted as Cm[i].
 * 
 * We repeat the same process on Emp and obstained Cmp[i], but here 0 < i < N - M since the length
 * of each sequence in Emp is M + 1.
 * 
 * The probability that any two embedding sequences in Em match is then sum(Cm) / (N - M +1).
 * We define Phi_m = sum(log(Cm)) / (N - M + 1) and Phi_mp = sum(log(Cmp)) / (N - M).
 * 
 * And the Apen is defined as Phi_m - Phi_mp.
 * 
 * @author peter
 *
 */

public class ApproximateEntropyOperator extends SisoOperator<Short, Double> {

	public ApproximateEntropyOperator() {
		super(Short.class);
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
