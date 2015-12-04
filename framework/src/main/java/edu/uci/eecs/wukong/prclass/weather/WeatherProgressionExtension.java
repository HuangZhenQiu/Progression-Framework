package edu.uci.eecs.wukong.prclass.weather;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.service.WeatherService;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class WeatherProgressionExtension extends AbstractProgressionExtension implements Initiable, TimerExecutable {
	private WeatherService service;
	private WeatherPrClass prClass;
	
	public WeatherProgressionExtension (WeatherPrClass prClass) {
		super(prClass);
		this.prClass = prClass;
	}
	
	// It is called after remote programming
	public void init() {
		service = new WeatherService(prClass.getCityId());
	}

	@WuTimer(interval = 2)
	public void execute() {
		short temp = service.getTemperature();
		prClass.setTemperature(temp);
	}
}
