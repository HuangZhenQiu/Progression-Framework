package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

import java.util.List;
public abstract class ProgressionExtension<T extends FeatureEntity, F extends PipelinePrClass> extends AbstractProgressionExtension<F> {
	private Object model;

	public ProgressionExtension(F plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	// Put trained model into the progression extension
	public void activate(Object model) {
		this.model = model;
	}
	
	// Triggered by general data pipeline
	public abstract List<ConfigurationCommand> execute(List<T> data, ExecutionContext context);
	
	// Triggered by context switch
	public abstract List<ConfigurationCommand> execute(BaseFactor context);
	
	// Triggered by timer
	public abstract List<ConfigurationCommand> execute();
}
