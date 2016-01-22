package edu.uci.eecs.wukong.framework.prclass;

/**
 * SystemPrClass provides an interface for managing FBP with monitoring capability.
 * 
 * 
 * 
 * @author peterhuang
 * 
 */
public abstract class SystemPrClass extends PipelinePrClass {

	public SystemPrClass(String name) {
		super(name, PrClass.PrClassType.SYSTEM_PRCLASS);
	}
	
	public void remap() {
		configManager.remapping("");
	}
}
