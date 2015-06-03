package edu.uci.eecs.wukong.framework.wkpf;

import java.util.Map;
import java.util.HashMap;

public class Model {

	public static class WuClass {
		private int wuclassId;
		private Map<Integer, String> properties;
		
		public WuClass(int wuclassId) {
			this.wuclassId = wuclassId;
			this.properties = new HashMap<Integer, String>();
		}
		
		public WuClass(int wuclassId, Map<Integer, String> properties) {
			this.wuclassId = wuclassId;
			this.properties = properties;
		}
		
		public void addProperty(Integer property, String name) {
			this.properties.put(property, name);
		}
	}
	
	public static class WuObject {
		private WuClass type;
		private int port;
		public WuObject(WuClass type, int port) {
			this.port = port;
			this.type = type;
		}
	}
}
