package edu.uci.eecs.wukong.edge.activity;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.property.Activity;

@WuClass(id = 10116)
public class ActivityRecgonitionEdgeClass extends EdgePrClass {

	@WuProperty(id = 0, name = "input", type = PropertyType.Input, dtype = DataType.Buffer)
	private Activity input;
	@WuProperty(id = 1, name = "output", type = PropertyType.Output)
	private short output;
	
	public ActivityRecgonitionEdgeClass(PrClassMetrics metrics) {
		super("ActivityRecgonition", metrics);
	}
	
	public enum ACTIVITY_TYPE {
		BATHE,
		BED_TOI_LET_TRANSITION,
		COOK,
		COOK_BREAKFAST,
		COOK_DINNER,
		COOK_LUNCH,
		DRESS,
		EAT,
		EAT_BREAKFAST,
		EAT_DINNER,
		EAT_LUNCH,
		ENTER_HOME,
		ENTERTAIN_GUESTS,
		EVENING_MDES,
		GROOM,
		LEAVE_HOME,
		MORNING_MEDS,
		PERSOANL_HYGIENE,
		PHONE,
		RELAX,
		SLEEP,
		SLEEP_OUT_OF_BED,
		TAKE_MEDICINE,
		TOILET,
		WASH_BREAKFAST_DISHES,
		WASH_DINNER_DISHES,
		WASH_DISHES,
		WASH_LUNCH_DISHES,
		WATCH_TV,
		WORK_AT_TABLE,
		WORK_ON_COMPUTER,
		WORK
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension> ();
		extensions.add(new ActivityFeatureExtractionExtension(this));
		extensions.add(new ActivityExecutionExtension(this));	
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		List<String> topics = new ArrayList<String> ();
		topics.add("Acvitiy_Model");
		return topics;
	}
	
	public void setOutput(short value) {
		if (value != output) {
			support.firePropertyChange("output", output, value);
			output = value;
		}
	}

}
