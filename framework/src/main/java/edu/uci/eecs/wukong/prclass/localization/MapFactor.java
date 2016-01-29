package edu.uci.eecs.wukong.prclass.localization;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.localization.Map;

public class MapFactor extends BaseFactor {
	private Map map;
	
	public MapFactor() {
		super("map");
	}
	
	public Map getMap() {
		return this.map;
	}
}
