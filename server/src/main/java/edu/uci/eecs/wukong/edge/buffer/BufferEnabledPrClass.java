package edu.uci.eecs.wukong.edge.buffer;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;

@WuClass(id = 10112)
public class BufferEnabledPrClass extends PipelinePrClass {
	@WuProperty(name = "sinput", id = 0, type = PropertyType.Input, dtype = DataType.Buffer)
	private short sinput;
	@WuProperty(name = "binput", id = 1, type = PropertyType.Input, dtype = DataType.Buffer)
	private byte binput;
	@WuProperty(name = "output", id = 2, type = PropertyType.Output)
	private short output;
	
	public BufferEnabledPrClass(PrClassMetrics metrics) {
		super("BufferEnabledPrClass", metrics);
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new BufferFeatureExtractionExtension(this));
		extensions.add(new BufferProgressionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		return null;
	}
	
	public void setAromaOnOff(short aroma_onoff) {
		this.support.firePropertyChange("ouput", this.output, output);
		this.output = aroma_onoff;
	}
}
