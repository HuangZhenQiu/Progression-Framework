package edu.uci.eecs.wukong.framework.model;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

public class InitValueTable {
	private List<InitValue> values;
	
	public InitValueTable() {
		values = new ArrayList<InitValue> ();
	}
	
	public void addInitValue(InitValue value) {
		values.add(value);
	}
	
	public List<InitValue> getValues() {
		return values;
	}
	
	public byte[] toByteArray() {
		int length = length();
		ByteBuffer buffer = ByteBuffer.allocate(length());
		buffer.put((byte) (length % 256));
		buffer.put((byte) (length / 256));
		for (InitValue value : values) {
			buffer.put(value.toByteArray());
		}
		
		return buffer.array();
	}
	
	public int length() {
		int length = 2;
		for (InitValue value : values) {
			length += value.length();
		}
		return length;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof InitValueTable) {
			InitValueTable model = (InitValueTable) object;
			if (model.values.equals(model.values)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		return values.hashCode();
	}
	
	@Override
	public String toString() {
		return values.toString();
	}
}
