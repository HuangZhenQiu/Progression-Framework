package edu.uci.eecs.wukong.framework.context;

public class DemoContext extends Context{
	public int ppnum1;
	public int ppnum2;
	public int ppnum3;
	public int ppnum4;
	public int ppnum5;
	public int ppnum6;
	
	public DemoContext() {
		super("Location");
	}
	
	public String toXML() {
		return "";
	}
	
	public String getElementName() {
		return "";
	}
}
