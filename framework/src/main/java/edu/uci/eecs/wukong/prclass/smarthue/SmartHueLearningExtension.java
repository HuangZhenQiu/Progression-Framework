package edu.uci.eecs.wukong.prclass.smarthue;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.regression.LinearRegression;

public class SmartHueLearningExtension extends LearningExtension<Short, SmartHue> {
	private double[] indoorLightAvr;   // y
	private double[] outdoorLightAvr;  // x
	private int number = 0;
	private int size = 1000;
	
	public SmartHueLearningExtension(SmartHue plugin) {
		super(plugin);
		this.indoorLightAvr = new double[size];
		this.outdoorLightAvr = new double[size];
	}

	@Override
	public void apply(List<Short> data, ExecutionContext context) {
		// Make sure user is in study
		if (context.getContext(SmartHue.LOCATION_TOPIC).equals("Bedroom") &&
				context.getContext(SmartHue.GESTURE_TOPIC).equals("Sitting")) {
			// accumulate enough samples
			indoorLightAvr[number % size] = data.get(0);
			outdoorLightAvr[number % size] = data.get(1);
			if (number % 1000 == 0) {
				this.setReady(true);
			}
		}
	}
	

	@Override
	public Object train() throws Exception {
		this.setReady(false);
		LinearRegression regression = new LinearRegression(outdoorLightAvr, indoorLightAvr);
		return regression;
	}
}
