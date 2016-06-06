package edu.uci.eecs.wukong.edge.dtdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class DemoFeatureExtension extends FeatureExtractionExtension {

	
	public DemoFeatureExtension(PipelinePrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public List<Operator> registerOperators() {
		List<Operator> operators = new ArrayList<Operator>();
		return operators;
	}

}
