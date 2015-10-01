package edu.uci.eecs.wukong.framework.model;

import java.util.HashMap;
import java.util.Map;

public class WuClassModel {
	private short wuclassId;
	private Map<String, Integer> properties;
	
	public WuClassModel(short wuclassId) {
		this.wuclassId = wuclassId;
		this.properties = new HashMap<String, Integer>();
	}
	
	public WuClassModel(short wuclassId, Map<String, Integer> properties) {
		this.wuclassId = wuclassId;
		this.properties = properties;
	}
	
	public int getPropertyId(String property) {
		if (properties.containsKey(property)) {
			return properties.get(property);
		}
		
		return -1;
	}
	
	public void addProperty(String name, Integer propertyId) {
		this.properties.put(name, propertyId);
	}
	
	public short getWuClassId() {
		return wuclassId;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof WuClassModel) {
			WuClassModel model = (WuClassModel) object;
			if (model.wuclassId == this.wuclassId
					&& model.properties.equals(this.properties)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.wuclassId + properties.hashCode();
	}
}
