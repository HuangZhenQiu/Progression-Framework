package edu.uci.eecs.wukong.prclass.dtdemo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.operator.Operator;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class DemoFeatureExtension extends FeatureAbstractionExtension {

	
	public DemoFeatureExtension(PrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	public List<Operator> registerOperators() {
		List<Operator> operators = new ArrayList<Operator>();
		return operators;
	}

}
