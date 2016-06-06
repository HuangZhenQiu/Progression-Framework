package edu.uci.eecs.wukong.edge.smarthue;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;;

@WuClass(id = 10114)
public class SmartHue extends PipelinePrClass {
	protected static String LOCATION_TOPIC = "location";
	protected static String GESTURE_TOPIC = "gesture";
	@WuProperty(name = "outdoorLightness", id = 0, type = PropertyType.Input, dtype = DataType.Buffer)
	private short outdoorLightness;
	@WuProperty(name = "indoorLightness", id = 1, type = PropertyType.Input, dtype = DataType.Buffer)
	private short indoorLightness;
	@WuProperty(name = "indoorLightness", id = 2, type = PropertyType.Input, dtype = DataType.Channel)
	private short indoorLightness2; // It is for getting feedback from control
	@WuProperty(name = "userAction", id = 3, type = PropertyType.Input, dtype = DataType.Channel)
	private short userAction; // It is for collect user's behavior
	@WuProperty(name = "hueOuput", id = 4, type = PropertyType.Output)
	private short hueOutput;
	
	public SmartHue(PrClassMetrics metrics) {
		super("SmartHue", metrics);
	}
	
	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new SmartHueFeatureExtractionExtension(this));
		extensions.add(new SmartHueLearningExtension(this));
		extensions.add(new SmartHueProgressionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		List<String> context = new ArrayList<String> ();
		context.add(LOCATION_TOPIC);
		context.add(GESTURE_TOPIC);
		return context;
	}
	
	public short getHueOutput() {
		return this.hueOutput;
	}
	
	public void setHueOutput(short output) {
		this.support.firePropertyChange("occupancy", this.hueOutput, output);
		this.hueOutput = output;
	}
}
