package edu.uci.eecs.wukong.prclass.smarthue;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.regression.LinearRegression;

public class SmartHueProgressionExtension extends AbstractProgressionExtension<SmartHue> implements
	Activatable, Executable, Channelable{
	private LinearRegression regression = null;
	private double indoorLightness;  // Latest Avr
	private double outdoorLightness; // Latest Avr
	private double targetLightness;
	private short controllerIndex;

	public SmartHueProgressionExtension(SmartHue plugin) {
		super(plugin);
	}

	@Override
	public void execute(List data, ExecutionContext context) {
		if (regression != null && data.size() == 2) {
			indoorLightness = (double) data.get(0);
			outdoorLightness = (double) data.get(1);
			targetLightness = regression.predict(outdoorLightness);
			
			if (targetLightness - indoorLightness > 5) {
				short targetValue = (short) (this.getPrClass().getHueOutput()  + 5 * controllerIndex);
				this.getPrClass().setHueOutput(targetValue);
			} else if (targetLightness - indoorLightness < -5) {
				short targetValue = (short) (this.getPrClass().getHueOutput()  - 5 * controllerIndex);
				this.getPrClass().setHueOutput(targetValue);
			}
		}
	}

	@Override
	public void execute(ChannelData data) {
		// get user's action or feedback from control commands
		if (data.getNpp().getPropertyId() == 3) { // Lightness feedback after control
			// TODO adaptively update the value, if the adjustment is not accurate enough
		} else if (data.getNpp().getPropertyId() == 4) { // user's action on hue controller
			// TODO tuning the parameter for controllerIndex;
		}
	}
	
	@Override
	public void activate(Object model) {
		regression = (LinearRegression) model;
	}
}
