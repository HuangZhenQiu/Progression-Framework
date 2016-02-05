package edu.uci.eecs.wukong.framework.model;

/**
 * Data type defines how property write data will be written
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 *
 */
public enum DataType {
    Channel("Channel"),
    Buffer("Buffer"),
    MixedBuffer("Mixed_Buffer"),
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
