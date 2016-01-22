package edu.uci.eecs.wukong.framework.prclass;

/**
 * SimplePrClass provides an interface similar with WuClass in Darjeeling. The updates from WKPF will be set to input properties through reflection.
 * At the mean time, if an output property is changed in update function. The value will be propagated to succeeding component of the FBP. It doesn't
 * provide RRD buffer and data pipeline support.
 * 
 * @author peterhuang
 *
 */
public abstract class SimplePrClass extends PrClass {

	protected SimplePrClass(String name) {
		super(name, false, false);
		// TODO Auto-generated constructor stub
	}

	public abstract void update();
}
