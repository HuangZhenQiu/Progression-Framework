package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

import java.util.List;
public abstract class PropertyProgressionExtension<T extends PipelinePrClass> extends AbstractExecutionExtension<T> 
	implements Activatable, Executable, FactorExecutable, TimerExecutable{
	
	public PropertyProgressionExtension(T plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	// Put trained model into the progression extension
	public void activate(Object model) {
		
	}
	
	// Triggered by general data pipeline
	public void execute(List data, ExecutionContext context) {
		
	}
	
	// Triggered by context switch
	public void execute(BaseFactor context) {
		
	}
	
	// Triggered by timer
	public void execute() {
		
	}
}
