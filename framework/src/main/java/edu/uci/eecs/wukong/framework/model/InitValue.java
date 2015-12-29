package edu.uci.eecs.wukong.framework.model;

import java.nio.ByteBuffer;

public class InitValue {
	private short componentId;
	private byte propertyNumber;
	private byte size;
	private byte[] value;
	
	public InitValue(short componentId,  byte propertyNumber, byte size, byte[] value) {
		this.componentId = componentId;
		this.propertyNumber = propertyNumber;
		this.size = size;
		this.value = value;
	}
	
	public byte[] toByteArray() {
		ByteBuffer buffer = ByteBuffer.allocate(length());
		buffer.putShort(componentId);
		buffer.put(propertyNumber);
		buffer.put(size);
		buffer.put(value);
		return buffer.array();
	}
	
	public int length() {
		return 4 + size;
	}

	public short getComponentId() {
		return componentId;
	}

	public void setComponentId(short componentId) {
		this.componentId = componentId;
	}

	public byte getPropertyNumber() {
		return propertyNumber;
	}

	public void setPropertyNumber(byte propertyNumber) {
		this.propertyNumber = propertyNumber;
	}

	public byte getSize() {
		return size;
	}

	public void setSize(byte size) {
		this.size = size;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}
	
	public int getLength() {
		return 4 + size;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof InitValue) {
			InitValue model = (InitValue) object;
			if (this.componentId == model.componentId
					&& this.propertyNumber == model.propertyNumber
					&& this.size == model.size
					&& this.value == model.value) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		int base = 33;
		int code = componentId * base + propertyNumber;
		code = code * base + size;
		code = code * base + value.hashCode();
		return code;
	}
	
	@Override
	public String toString() {
		return "InitValue[ComponentId = " + componentId + ", PropertyNumber = " + propertyNumber + ", Size = " + size + ", Value =" + value + "]";
	}
}
