package edu.uci.eecs.wukong.prclass.smarthue;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Activatable;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.regression.LogisticRegression;

public class SmartHueProgressionExtension extends AbstractProgressionExtension implements
	Activatable, Executable, Channelable{
	private LogisticRegression regression = null;
	private short indoorLightness;  // Latest Avr
	private short outdoorLightness; // Latest Avr

	public SmartHueProgressionExtension(PipelinePrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List data, ExecutionContext context) {
		if (regression != null) {
		}
	}

	@Override
	public void execute(ChannelData data) {
		// get user's action or feedback from control commands
		if (data.getNpp().getPropertyId() == 3) { // Lightness feedback after control
			
		} else if (data.getNpp().getPropertyId() == 4) { // user's action on hue controller
			
		}
	}
	
	@Override
	public void activate(Object model) {
		regression = (LogisticRegression) model;
	}
}
