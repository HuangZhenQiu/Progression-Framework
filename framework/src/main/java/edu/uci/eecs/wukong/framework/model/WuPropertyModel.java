package edu.uci.eecs.wukong.framework.model;

import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;

public class WuPropertyModel {
	private int id;
	private String name;
	private PropertyType ptype;
	private DataType dtype;
	
	public WuPropertyModel(int id, String name, PropertyType pType, DataType dType) {
		this.id = id;
		this.name = name;
		this.ptype = pType;
		this.dtype = dType;
	}
	
	public WuPropertyModel(WuProperty property) {
		this.id = property.id();
		this.name = property.name();
		this.ptype = property.type();
		this.dtype = property.dtype();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PropertyType getPtype() {
		return ptype;
	}

	public void setPtype(PropertyType ptype) {
		this.ptype = ptype;
	}

	public DataType getDtype() {
		return dtype;
	}

	public void setDtype(DataType dtype) {
		this.dtype = dtype;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof WuPropertyModel) {
			WuPropertyModel model = (WuPropertyModel) object;
			if (this.id == model.id
					&& this.name.equals(model.name)
					&& this.ptype.equals(model.ptype)
					&& this.dtype.equals(model.dtype)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = id * base;
		code = code * base + name.hashCode();
		code = code * base + ptype.hashCode();
		code = code * base + dtype.hashCode();	
		return code;
	}
}
