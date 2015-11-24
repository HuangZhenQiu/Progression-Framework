package edu.uci.eecs.wukong.prclass.weather;

import java.util.List;
import java.util.ArrayList;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.prclass.weather.WeatherProgressionExtension;

public class WeatherPrClass extends PrClass {
	@WuProperty(name = "cityId", id = 1, type = PropertyType.Input, dtype = DataType.Init_Value)
	private int cityId;
	@WuProperty(name = "temperature", id = 2, type = PropertyType.Output)
	private short temperature;

	public WeatherPrClass() {
		super("WeatherPrClass");
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new WeatherProgressionExtension(this));
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
