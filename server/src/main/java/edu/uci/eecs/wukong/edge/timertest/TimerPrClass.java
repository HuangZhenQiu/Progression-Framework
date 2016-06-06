package edu.uci.eecs.wukong.edge.timertest;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.edge.timertest.TimerTestProgressionExtension;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;


/**
 * An Example PrClass to periodically send signal to another component.
 * It is an example for TimerExecutable enabled progression extension, and
 * also be used to integrated test on dirty property propagation of a FBP.
 *
 */

@WuClass(id = 10002)
public class TimerPrClass extends PipelinePrClass{
	
	@WuProperty(name = "output", id = 0, type = PropertyType.Output, dtype = DataType.Short)
	private short output;
	
	public TimerPrClass(PrClassMetrics metrics) {
		super("TimerPrClass", metrics);
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
	
	public void setOutput(short newOutput) {
		this.support.firePropertyChange("output", output, newOutput);
		this.output = newOutput;
	}
}
