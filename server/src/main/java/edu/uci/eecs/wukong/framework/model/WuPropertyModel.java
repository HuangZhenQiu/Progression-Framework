package edu.uci.eecs.wukong.framework.model;

import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;

public class WuPropertyModel {
	private byte id;
	private String name;
	private Class<?> type;
	private PropertyType ptype;
	private DataType dtype;
	private int capacity;
	private int interval;
	private int timeUnit;
	
	public WuPropertyModel(byte id, String name, PropertyType pType, DataType dType, Class<?> type) {
		this.id = id;
		this.name = name;
		this.ptype = pType;
		this.dtype = dType;
		this.type = type;
	}
	
	public WuPropertyModel(WuProperty property, Class<?> type) {
		this.id = property.id();
		this.name = property.name();
		this.ptype = property.type();
		this.dtype = property.dtype();
		this.capacity = property.capacity();
		this.interval = property.interval();
		this.timeUnit = property.timeUnit();
		this.type = type;
	}
	
	public byte getId() {
		return id;
	}

	public void setId(byte id) {
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

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(int timeUnit) {
		this.timeUnit = timeUnit;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof WuPropertyModel) {
			WuPropertyModel model = (WuPropertyModel) object;
			if (this.id == model.id
					&& this.name.equals(model.name)
					&& this.ptype.equals(model.ptype)
					&& this.dtype.equals(model.dtype)
					&& this.type.equals(model.type)) {
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
		code = code * base + type.hashCode();
		return code;
	}
}
