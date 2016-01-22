package edu.uci.eecs.wukong.prclass.smarthue;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.regression.Instance;
import edu.uci.eecs.wukong.framework.regression.LogisticRegression;

public class SmartHueLearningExtension extends LearningExtension<Short> {
	private List<Instance> instances;
	private int num = 0;
	
	public SmartHueLearningExtension(PipelinePrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
		instances = new ArrayList<Instance> ();
	}

	@Override
	public void apply(List<Short> data, ExecutionContext context) {
		// Make sure user is in study
		if (context.getContext(SmartHue.LOCATION_TOPIC).equals("Bedroom") &&
				context.getContext(SmartHue.GESTURE_TOPIC).equals("Sitting")) {
			// accumulate enough samples
			Instance instance = new Instance(instances.size(), toArray(data));
			if (instances.size() == 10000) {
				this.setReady(true);
			}
		}
	}
	
	private int[] toArray(List<Short> data) {
		int[] array = new int[data.size()];
		int i = 0;
		for (Short s : data) {
			array[i++] = s.intValue();
		}
		
		return array;
	}
	

	@Override
	public Object train() throws Exception {
		this.setReady(false);
		LogisticRegression regression = new LogisticRegression(2);
		regression.train(instances);
		instances.clear();
		return regression;
	}
}
