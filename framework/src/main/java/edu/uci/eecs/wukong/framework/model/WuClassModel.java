package edu.uci.eecs.wukong.framework.model;

import java.util.HashMap;
import java.util.Map;

import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.WuPropertyModel;

public class WuClassModel {
	private short wuclassId;
	private Map<String, WuPropertyModel> properties;
	private Map<Byte, WuPropertyModel> idToProperties;
	
	public WuClassModel(short wuclassId) {
		this.wuclassId = wuclassId;
		this.properties = new HashMap<String, WuPropertyModel>();
		this.idToProperties = new HashMap<Byte, WuPropertyModel>();
	}
	
	public int getPropertyId(String property) {
		if (properties.containsKey(property)) {
			return properties.get(property).getId();
		}
		
		return -1;
	}
	
	public WuPropertyModel getPropertyModel(byte propertyId) {
		return idToProperties.get(Byte.valueOf(propertyId));
	}
	
	public void addProperty(String name, WuProperty property) {
		WuPropertyModel model = new WuPropertyModel(property);
		this.properties.put(name, model);
		this.idToProperties.put(property.id(), model);
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
	
	@Override
	public String toString() {
		return "WuClass[id = " + this.wuclassId + "]";
	}
}
