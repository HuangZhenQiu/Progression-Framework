package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

import java.util.List;
public abstract class PropertyProgressionExtension<T extends FeatureEntity> extends AbstractExtension {
	
	public PropertyProgressionExtension(Plugin plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	// Put trained model into the progression extension
	public void activate(Object model) {
		
	}
	
	// Triggered by general data pipeline
	public void execute(List<T> data, ExecutionContext context) {
		
	}
	
	// Triggered by context switch
	public void execute(Context context) {
		
	}
	
	// Triggered by timer
	public void execute() {
		
	}
}
