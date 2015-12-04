package edu.uci.eecs.wukong.framework.model;

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
