package edu.uci.eecs.wukong.edge.localization;

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
