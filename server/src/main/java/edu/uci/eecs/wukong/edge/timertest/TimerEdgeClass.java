package edu.uci.eecs.wukong.edge.timertest;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.edge.timertest.TimerTestExecutionExtension;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;


/**
 * An Example PrClass to periodically send signal to another component.
 * It is an example for TimerExecutable enabled progression extension, and
 * also be used to integrated test on dirty property propagation of a FBP.
 *
 */

@WuClass(id = 10002)
public class TimerEdgeClass extends EdgePrClass{
	
	@WuProperty(name = "output", id = 0, type = PropertyType.Output, dtype = DataType.Short)
	private boolean output;
	
	public TimerEdgeClass(PrClassMetrics metrics) {
		super("TimerPrClass", metrics);
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new TimerTestExecutionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setOutput(boolean newOutput) {
		this.support.firePropertyChange("output", output, newOutput);
		this.output = newOutput;
	}
	
	public boolean getOutput() {
		return this.getOutput();
	}
}
