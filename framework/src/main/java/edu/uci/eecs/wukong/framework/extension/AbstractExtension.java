package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public abstract class AbstractExtension<T extends PipelinePrClass> implements Extension {
	protected T prClass;
	public AbstractExtension(T prClass) {
		this.prClass = prClass;
	}
	
	public void setup() {
		
	}
	
	public void clean(ExecutionContext context) {
		
	}

	public T getPrClass() {
		return prClass;
	}
	
	public byte getPortId() {
		return prClass.getPortId();
	}
}
