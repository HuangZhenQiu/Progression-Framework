package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.ContextExecutable;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.context.BaseContext;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

import java.util.List;
public abstract class PropertyProgressionExtension<T extends FeatureEntity> extends AbstractProgressionExtension<T> 
	implements Activatable, Executable<T>, ContextExecutable, TimerExecutable{
	
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
	public void execute(BaseContext context) {
		
	}
	
	// Triggered by timer
	public void execute() {
		
	}
}
