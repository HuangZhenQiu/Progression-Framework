package edu.uci.eecs.wukong.prclass.timertest;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

/**
 * An Example PrClass to periodically send signal to another component.
 * It is an example for TimerExecutable enabled progression extension, and
 * also be used to integrated test on dirty property propagation of a FBP.
 *
 */
public class TimerPrClass extends PrClass{
	
	private short output;
	
	public TimerPrClass() {
		super("TimerPrClass");
	}

	@Override
	public List<Extension> registerExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> registerContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
