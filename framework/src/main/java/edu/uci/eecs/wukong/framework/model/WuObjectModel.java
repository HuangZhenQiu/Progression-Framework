package edu.uci.eecs.wukong.framework.model;

import edu.uci.eecs.wukong.framework.model.WuClassModel;

public class WuObjectModel {
	private WuClassModel type;
	private int pluginId;
	private byte port;
	public WuObjectModel(WuClassModel type, byte port, int pluginId) {
		this.port = port;
		this.type = type;
		this.pluginId = pluginId;
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
	
	public int getPluginId() {
		return this.pluginId;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof WuObjectModel) {
			WuObjectModel model = (WuObjectModel) object;
			if (model.pluginId == this.pluginId
					&& model.port == this.port
					&& model.type.equals(this.type)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = pluginId * base;
		code = code * base + port;
		return code + type.hashCode();
	}
}
