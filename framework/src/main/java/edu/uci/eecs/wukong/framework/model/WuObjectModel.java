package edu.uci.eecs.wukong.framework.model;

import edu.uci.eecs.wukong.framework.model.WuClassModel;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class WuObjectModel {
	private WuClassModel type;
	private PrClass plugin;
	public WuObjectModel(WuClassModel type, PrClass plugin) {
		this.plugin = plugin;
		this.type = type;
	}
	
	public byte getPropertyId(String property) {
		if (type != null) {
			return type.getPropertyId(property);
		}
		
		return -1;
	}
	
	public PrClass getPlugin() {
		return this.plugin;
	}
	
	public byte getPort() {
		return this.plugin.getPortId();
	}
	
	public WuClassModel getType() {
		return this.type;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof WuObjectModel) {
			WuObjectModel model = (WuObjectModel) object;
			if (model.plugin.getPortId() == this.plugin.getPortId()
					&& model.type.equals(this.type)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = plugin.getPortId();
		return code * base + type.hashCode();
	}
}
