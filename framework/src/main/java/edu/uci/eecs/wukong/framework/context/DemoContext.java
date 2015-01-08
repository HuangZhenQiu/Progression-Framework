package edu.uci.eecs.wukong.framework.context;

public class DemoContext extends Context{
	public int ppnum1;
	public int ppnum2;
	public int ppnum3;
	public int ppnum4;
	public int ppnum5;
	public int ppnum6;
	
	public boolean equals(DemoContext context) {
		if(this.ppnum1 == context.ppnum1 && this.ppnum2 == context.ppnum2
				&& this.ppnum3 == context.ppnum3 && this.ppnum4 == context.ppnum4
				&& this.ppnum5 == context.ppnum5 && this.ppnum6 == context.ppnum6) {
			return true;
		}
		
		return false;
	}
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
