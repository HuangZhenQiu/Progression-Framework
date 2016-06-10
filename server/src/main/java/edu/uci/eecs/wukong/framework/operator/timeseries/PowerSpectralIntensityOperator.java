package edu.uci.eecs.wukong.framework.operator.timeseries;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.operator.SimoOperator;

public class PowerSpectralIntensityOperator extends SimoOperator<Short, Double> {
	private static Logger logger = LoggerFactory.getLogger(PowerSpectralIntensityOperator.class);
	private double[] bands = { 0.5, 4, 7, 12, 30 };
	private double frequency = 100;
	private FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

	public PowerSpectralIntensityOperator() {
		super(Short.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Double> operate(List<DataPoint<Short>> data) {
		logger.info("PowerSpectralIntensityOperator is executed");

		int lastIndex = 0, index = 0, i = 0;
		while (index < data.size()) {
			lastIndex = index;
			index = (int) Math.pow(2.0, i);
			i++;
		}

		double[] raw = new double[lastIndex];
		for (i = 0; i < lastIndex; i++) {
			raw[i] = data.get(i).getValue().doubleValue();
		}

		Complex[] c = fft.transform(raw, TransformType.FORWARD);
		List<Double> power = new ArrayList<Double>();
		for (int freqIndex = 0; freqIndex < bands.length - 1; freqIndex++) {
			double freq = bands[freqIndex];
			double nextFreq = bands[freqIndex + 1];
			int lower = (int) ((freq / frequency) * data.size());
			int upper = (int) ((nextFreq / frequency) * data.size());
			double sum = 0;
			for (int j = lower; j < upper; j++) {
				sum += c[j].abs();
			}
			power.add(sum);
		}
		return power;
	}

}
