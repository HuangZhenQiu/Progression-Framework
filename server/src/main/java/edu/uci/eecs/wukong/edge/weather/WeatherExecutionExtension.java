package edu.uci.eecs.wukong.edge.weather;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.service.WeatherService;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class WeatherExecutionExtension extends AbstractExecutionExtension implements Initiable, TimerExecutable {
	private WeatherService service;
	private WeatherEdgeClass prClass;
	
	public WeatherExecutionExtension (WeatherEdgeClass prClass) {
		super(prClass);
		this.prClass = prClass;
	}
	
	// It is called after remote programming
	public boolean init() {
		service = new WeatherService(prClass.getCityId());
		return true;
	}

	@WuTimer(interval = 2)
	public void execute() {
		short temp = service.getTemperature();
		prClass.setTemperature(temp);
	}
}
