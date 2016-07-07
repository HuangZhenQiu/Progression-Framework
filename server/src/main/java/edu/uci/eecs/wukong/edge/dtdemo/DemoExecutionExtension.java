package edu.uci.eecs.wukong.edge.dtdemo;

import java.util.List;

import weka.classifiers.trees.M5P;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.extension.ExecutionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;


public class DemoExecutionExtension extends ExecutionExtension<DemoFeatureEntity, DemoEdgeClass> {
	private M5P model;
	
	public DemoExecutionExtension(DemoEdgeClass plugin) {
		super(plugin);
	}
	
	public void activate(Object model) {
		this.model = (M5P)model;
	}

	public List<ConfigurationCommand> execute(List<DemoFeatureEntity> data,
			ExecutionContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ConfigurationCommand> execute(BaseFactor context) {
		throw new UnsupportedOperationException();
	}

	public List<ConfigurationCommand> execute() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
