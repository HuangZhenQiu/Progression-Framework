package edu.uci.eecs.wukong.prclass.dtdemo;

import java.util.List;

import weka.classifiers.trees.M5P;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class DemoProgressionExtension extends ProgressionExtension<DemoFeatureEntity> {
	private M5P model;
	
	public DemoProgressionExtension(PipelinePrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}
	
	public void activate(Object model) {
		// TODO Auto-generated method stub
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
