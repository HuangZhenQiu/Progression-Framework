package edu.uci.eecs.wukong.prclass.eeg;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.operator.timeseries.PowerSpectralIntensityOperator;

public class EEGFeatureExtractionExtension extends FeatureExtractionExtension<EEGPrClass> {

	public EEGFeatureExtractionExtension(EEGPrClass plugin) {
		super(plugin);
	}

	@Override
	public List<Operator<?>> registerOperators() {
		List<Operator<?>> operators = new ArrayList<Operator<?>> ();
		PowerSpectralIntensityOperator  psi = new PowerSpectralIntensityOperator();
		psi.addDataSource(0, 3);
		operators.add(psi); 
		return operators;
	}

}
