package edu.uci.eecs.wukong.framework.model;

public enum DataType {
    Channel("Channel"),
    Buffer("Buffer"),
    Byte("Byte"),
    Short("Short"),
    RefreshRate("RefreshRate");
    
	private final String name;
	private DataType(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
}
