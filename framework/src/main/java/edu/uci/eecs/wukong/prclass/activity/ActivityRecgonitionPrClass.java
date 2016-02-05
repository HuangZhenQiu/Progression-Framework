package edu.uci.eecs.wukong.prclass.activity;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

@WuClass(id = 10115)
public class ActivityRecgonitionPrClass extends PipelinePrClass {

	@WuProperty(id = 0, name = "input", type = PropertyType.Input, dtype = DataType.Buffer)
	private short input;
	@WuProperty(id = 0, name = "output", type = PropertyType.Output)
	private short output;
	
	protected ActivityRecgonitionPrClass(PrClassMetrics metrics) {
		super("ActivityRecgonition", metrics);
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new ActivityFeatureExtractionExtension(this));
		extensions.add(new ActivityProgressionExtension(this));	
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		List<String> topics = new ArrayList<String> ();
		topics.add("Acvitiy_Model");
		return topics;
	}

}
