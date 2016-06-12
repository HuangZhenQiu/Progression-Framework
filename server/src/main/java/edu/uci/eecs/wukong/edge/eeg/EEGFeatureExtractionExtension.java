package edu.uci.eecs.wukong.edge.eeg;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.operator.timeseries.RelativeIntensiveRatioOperator;

public class EEGFeatureExtractionExtension extends FeatureExtractionExtension<EEGPrClass> {

	public EEGFeatureExtractionExtension(EEGPrClass plugin) {
		super(plugin);
	}

	@Override
	public List<Operator<?>> registerOperators() {
		List<Operator<?>> operators = new ArrayList<Operator<?>> ();
		RelativeIntensiveRatioOperator  psi = new RelativeIntensiveRatioOperator();
		psi.addDataSource(0, 5);
		operators.add(psi); 
		return operators;
	}

}
