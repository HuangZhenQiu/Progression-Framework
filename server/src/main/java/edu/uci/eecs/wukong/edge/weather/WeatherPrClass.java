package edu.uci.eecs.wukong.edge.weather;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.edge.weather.WeatherExecutionExtension;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.DataType;

@WuClass(id = 10111)
public class WeatherPrClass extends PipelinePrClass {
	@WuProperty(name = "cityId", id = 0, type = PropertyType.Input, dtype = DataType.Init_Value)
	private int cityId;
	@WuProperty(name = "temperature", id = 1, type = PropertyType.Output)
	private short temperature;

	public WeatherPrClass(PrClassMetrics metrics) {
		super("WeatherPrClass", metrics);
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new WeatherExecutionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getCityId() {
		return cityId;
	}
	
	public void setTemperature(short newTemperature) {
		short oldValue = this.temperature;
		this.temperature = newTemperature;
		this.support.fireIndexedPropertyChange("temperature", 2, oldValue, newTemperature);
	}
}
