package edu.uci.eecs.wukong.framework.entity;

import java.util.Map;
import java.util.HashMap;
public class HueEntity implements Entity {
	private Map<String, String> content;
	
	public HueEntity() {
		content = new HashMap<String, String>();
		content.put("on", "false");
	}
	
	public HueEntity(int sat, int bri, int hue) {
		content.put("on", "true");
		content.put("sat", Integer.toString(sat));
		content.put("bri", Integer.toString(bri));
		content.put("hue", Integer.toString(hue));
	}
	
	public Map<String, String> getContent() {
		return content;
	}
}
