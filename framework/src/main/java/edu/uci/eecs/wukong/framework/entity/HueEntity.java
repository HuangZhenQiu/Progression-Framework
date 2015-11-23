package edu.uci.eecs.wukong.framework.entity;

import java.util.Map;
import java.util.HashMap;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class HueEntity extends Entity {
	private boolean isOn;
	private Map<String, Object> content;
	
	public HueEntity(PrClass prClass) {
		super(prClass);
		content = new HashMap<String, Object>();
		isOn = false;
		content.put("on", isOn);
	}
	
	public HueEntity(PrClass prClass, int sat, int bri, int hue) {
		super(prClass);
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
