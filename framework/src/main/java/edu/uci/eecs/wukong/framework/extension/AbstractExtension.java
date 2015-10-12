package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public abstract class AbstractExtension implements Extension {
	protected PrClass prClass;
	public AbstractExtension(PrClass prClass) {
		this.prClass = prClass;
	}
	
	public void setup() {
		
	}
	
	public void clean(ExecutionContext context) {
		
	}

	public PrClass getPrClass() {
		return prClass;
	}
	
	public byte getPortId() {
		return prClass.getPortId();
	}
}
