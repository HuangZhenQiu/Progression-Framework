package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SisoOperator;
import edu.uci.eecs.wukong.framework.util.OperatorUtil;

/**
 * Compute spectral entropy of a time series from either two cases below:
 * 
 * 
 * 
 * @author peter
 *
 */
public class SpectralEntropyOperator extends SisoOperator<Short, Double> {
	private RelativeIntensiveRatioOperator ratio = new RelativeIntensiveRatioOperator();
	public SpectralEntropyOperator() {
		super(Short.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Double operate(List<DataPoint<Short>> data) throws Exception {
		double[] x = OperatorUtil.extractDoubleValues(data);
		List<Double> powerRatio = ratio.operate(data);
		
		double spectralEntropy = 0;
		for (int i = 0; i < powerRatio.size(); i++) {
			spectralEntropy += powerRatio.get(i) * Math.log(powerRatio.get(i));
		}
		
		spectralEntropy /= Math.log(powerRatio.size());
		
		return -1 * spectralEntropy;
	}

}
