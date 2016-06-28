package edu.uci.eecs.wukong.framework.model;

public class SensorProperty {
	private int wuClassId;
	private int propertyId;
	
	public SensorProperty(int wuClassId, int propertyId) {
		this.wuClassId = wuClassId;
		this.propertyId = propertyId;
	}
	
	public boolean equals(Object object) {
		if (object instanceof SensorProperty) {
			SensorProperty model = (SensorProperty) object;
			if (model.wuClassId == this.wuClassId
					&& model.propertyId == this.propertyId) {
				return true;
			}
				
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = wuClassId;
		code = code * base + propertyId;
		return code;
	}
	
	@Override
	public String toString() {
		return "SensorProperty[wuClassId = " + wuClassId
				+ ", propertyId = " + propertyId + "]";
	}
}
