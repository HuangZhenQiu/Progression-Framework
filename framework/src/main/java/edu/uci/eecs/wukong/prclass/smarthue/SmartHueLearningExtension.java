package edu.uci.eecs.wukong.prclass.smarthue;

import java.util.List;
import java.util.Map.Entry;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class SmartHueLearningExtension extends LearningExtension<Short> {
	private List<Entry<Short, Short>> pairs;
	
	public SmartHueLearningExtension(PrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void apply(List<Short> data, ExecutionContext context) {
		if (context.getContext(SmartHue.LOCATION_TOPIC).equals("Bedroom") &&
				context.getContext(SmartHue.GESTURE_TOPIC).equals("Sitting")) {
		
			if (pairs.size() == 10000) {
				this.setReady(true);
			}
		}
	}

	@Override
	public Object train() throws Exception {
		this.setReady(false);
		
		
		return null;
	}
}
