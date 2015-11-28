package edu.uci.eecs.wukong.framework.service;

import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

public class WeatherService extends AbstractHttpService {
	private static Logger logger = LoggerFactory.getLogger(WeatherService.class);
	private static final String appId = "2de143494c0b295cca9337e1e96b00e0";
	private static JsonParser parser = new JsonParser();
	private int cityId;
	public WeatherService(int cityId) {
		super("OpenWeatherMap", "api.openweathermap.org", "80",
				"data/2.5/weather?id=" + cityId + "&appid=" + appId);
		this.cityId = cityId;
	}
	
	public WeatherService(String name, String ip, String port, String method) {
		super(name, ip, port, method);
	}
	
	public short getTemperature() {
		String json  = this.send(new HttpGet(this.CONFIG_URL), null);
		logger.debug("Received json message by calling weather service : " + json);
		JsonElement root = parser.parse(json);
		short temp =  root.getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsShort();
		logger.info("Get temperature from weather service for city " + cityId + " whose temperature is " + temp);	
		return temp;
	}
}
