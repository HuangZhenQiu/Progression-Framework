package edu.uci.eecs.wukong.framework.model;

public enum DataType {
    Channel("Channel"),
    Buffer("Buffer"),
    Init_Value("Init_Value"),
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
