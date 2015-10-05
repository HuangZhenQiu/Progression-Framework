package edu.uci.eecs.wukong.framework.model;

public enum PropertyType {
	Input("Input"),
	Output("Output");
	
	private final String name;
	
	private PropertyType(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
}
