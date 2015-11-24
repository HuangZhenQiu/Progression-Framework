package edu.uci.eecs.wukong.framework.service;

import org.apache.http.client.methods.HttpGet;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;

public class WeatherService extends AbstractHttpService {
	private static final String appId = "2de143494c0b295cca9337e1e96b00e0";
	private static JsonParser parser = new JsonParser();
	public WeatherService(int cityId) {
		super("OpenWeatherMap", "http://api.openweathermap.org/", "80",
				"data/2.5/weather?id=" + cityId + "&appid=" + appId);
	}
	
	public WeatherService(String name, String ip, String port, String method) {
		super(name, ip, port, method);
		// TODO Auto-generated constructor stub
	}
	
	public short getTemperature() {
		String json  = this.send(new HttpGet(this.CONFIG_URL), null);
		JsonElement root = parser.parse(json);
		return root.getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsShort();
	}
}
