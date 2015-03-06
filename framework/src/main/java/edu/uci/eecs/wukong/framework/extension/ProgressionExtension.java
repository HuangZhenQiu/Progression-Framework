package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;

import java.util.List;
public interface ProgressionExtension<T extends FeatureEntity> extends Extension<T> {
	
	// Put trained model into the progression extension
	public void activate(Object model);
	
	// Triggered by general data pipeline
	public List<ConfigurationCommand> execute(List<T> data, ExecutionContext context);
	
	// Triggered by context switch
	public List<ConfigurationCommand> execute(Context context);
	
	// Triggered by timer
	public List<ConfigurationCommand> execute();
}
