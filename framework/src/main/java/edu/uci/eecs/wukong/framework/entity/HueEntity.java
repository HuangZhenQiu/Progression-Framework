package edu.uci.eecs.wukong.framework.entity;

import java.util.Map;
import java.util.HashMap;
public class HueEntity implements Entity {
	private boolean isOn;
	private Map<String, Object> content;
	
	public HueEntity() {
		content = new HashMap<String, Object>();
		isOn = false;
		content.put("on", isOn);
	}
	
	public HueEntity(int sat, int bri, int hue) {
		content = new HashMap<String, Object>();
		isOn = true;
		content.put("on", isOn);
		content.put("sat", sat);
		content.put("bri", bri);
		content.put("hue", hue);
	}
	
	public Map<String, Object> getContent() {
		return content;
	}
}
