package edu.uci.eecs.wukong.prclass.buffer;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.operator.MaxOperator;
import edu.uci.eecs.wukong.framework.operator.MinOperator;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class BufferFeatureExtractionExtension extends
		FeatureExtractionExtension {

	public BufferFeatureExtractionExtension(PipelinePrClass plugin) {
		super(plugin);
	}

	@Override
	public List<Operator> registerOperators() {
		List<Operator> operators = new ArrayList<Operator> ();
		MaxOperator<Short> max =  new MaxOperator<Short>();
		max.addDataSource(1, 5);
		operators.add(max);
		
		MinOperator<Byte> min = new MinOperator<Byte>();
		min.addDataSource(2, 5);
		operators.add(min);
		return operators;
	}

}
