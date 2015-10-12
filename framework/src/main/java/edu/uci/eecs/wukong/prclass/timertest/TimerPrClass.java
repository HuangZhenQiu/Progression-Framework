package edu.uci.eecs.wukong.prclass.timertest;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.prclass.timertest.TimerTestProgressionExtension;


/**
 * An Example PrClass to periodically send signal to another component.
 * It is an example for TimerExecutable enabled progression extension, and
 * also be used to integrated test on dirty property propagation of a FBP.
 *
 */

@WuClass(id = 10002)
public class TimerPrClass extends PrClass{
	
	@WuProperty(name = "light", id = 4, type = PropertyType.Output, dtype = DataType.Short)
	private short output;
	
	public TimerPrClass() {
		super("TimerPrClass");
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new TimerTestProgressionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setOuput(short output) {
		this.output = output;
	}
}
