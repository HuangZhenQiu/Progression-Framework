package edu.uci.eecs.wukong.edge.buffer;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.operator.basic.MaxOperator;
import edu.uci.eecs.wukong.framework.operator.basic.MinOperator;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;

public class BufferFeatureExtractionExtension extends
		FeatureExtractionExtension {

	public BufferFeatureExtractionExtension(EdgePrClass plugin) {
		super(plugin);
	}

	@Override
	public List<Operator> registerOperators() {
		List<Operator> operators = new ArrayList<Operator> ();
		MaxOperator max =  new MaxOperator();
		max.addDataSource(1, 5);
		operators.add(max);
		
		MinOperator min = new MinOperator();
		min.addDataSource(2, 5);
		operators.add(min);
		return operators;
	}

}
