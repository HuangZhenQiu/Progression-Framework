package edu.uci.eecs.wukong.framework.model;

import edu.uci.eecs.wukong.framework.model.WuClassModel;

public class WuObjectModel {
	private WuClassModel type;
	private byte port;
	public WuObjectModel(WuClassModel type, byte port) {
		this.port = port;
		this.type = type;
	}
	
	public byte getPropertyId(String property) {
		if (type != null) {
			type.getPropertyId(property);
		}
		
		return -1;
	}
	
	public byte getPort() {
		return this.port;
	}
	
	public WuClassModel getType() {
		return this.type;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof WuObjectModel) {
			WuObjectModel model = (WuObjectModel) object;
			if (model.port == this.port
					&& model.type.equals(this.type)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = port;
		return code * base + type.hashCode();
	}
}
