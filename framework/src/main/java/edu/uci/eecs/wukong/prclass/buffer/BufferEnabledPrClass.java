package edu.uci.eecs.wukong.prclass.buffer;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;

@WuClass(id = 10112)
public class BufferEnabledPrClass extends PrClass {
	@WuProperty(name = "sInput", id = 1, type = PropertyType.Input, dtype = DataType.Buffer)
	private short sInput;
	@WuProperty(name = "bInput", id = 2, type = PropertyType.Input, dtype = DataType.Buffer)
	private byte bInput;
	@WuProperty(name = "output", id = 3, type = PropertyType.Output)
	private short output;
	
	public BufferEnabledPrClass() {
		super("BufferEnabledPrClass");
	}

	@Override
	public List<Extension> registerExtension() {
		return null;
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
