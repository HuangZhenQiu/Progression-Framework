package edu.uci.eecs.wukong.edge.eeg;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

@WuClass(id = 10201)
public class EEGPrClass extends PipelinePrClass {
	@WuProperty(name = "raw", id = 0, type = PropertyType.Input,
			dtype = DataType.Buffer, capacity = 2000, interval = 1000, timeUnit = 30)
	private short raw;
	@WuProperty(name = "output", id = 1, type = PropertyType.Output)
	private boolean output;

	public EEGPrClass(PrClassMetrics metrics) {
		super("EEGPrClass", metrics);
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new EEGFeatureExtractionExtension(this));
		extensions.add(new EEGExecutionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setOutput(boolean output) {
		if (output != this.output) {
			this.support.firePropertyChange("output", this.output, output);
			this.output = output;
		}
	}

}
