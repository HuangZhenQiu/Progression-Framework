package edu.uci.eecs.wukong.edge.occupancy;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.operator.basic.ExistenceOperator;

public class ODFeatureExtractionExtension extends FeatureExtractionExtension {

	public ODFeatureExtractionExtension(OccupancyDetection prClass) {
		super(prClass);
	}

	@Override
	public List<Operator> registerOperators() {
		List<Operator> operators = new ArrayList<Operator>();
		ExistenceOperator existence = new ExistenceOperator();
		// Find the existence signal every 15 minutes for property 1
		existence.addDataSource(1, 15 * 60  * 1000);
		operators.add(existence);
		return operators;
	}
}
