package edu.uci.eecs.wukong.framework.model;

import edu.uci.eecs.wukong.framework.model.WuClassModel;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class WuObjectModel {
	private WuClassModel type;
	private PrClass prClass;
	public WuObjectModel(WuClassModel type, PrClass prClass) {
		this.prClass = prClass;
		this.type = type;
	}
	
	public byte getPropertyId(String property) {
		if (type != null) {
			return type.getPropertyId(property);
		}
		
		return -1;
	}
	
	public PrClass getPrClass() {
		return this.prClass;
	}
	
	public byte getPort() {
		return this.prClass.getPortId();
	}
	
	public WuClassModel getType() {
		return this.type;
	}
	
	public boolean isValid() {
		return this.prClass != null && this.type != null;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof WuObjectModel) {
			WuObjectModel model = (WuObjectModel) object;
			if (model.prClass.getPortId() == this.prClass.getPortId()
					&& model.type.equals(this.type)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = prClass.getPortId();
		return code * base + type.hashCode();
	}
}
