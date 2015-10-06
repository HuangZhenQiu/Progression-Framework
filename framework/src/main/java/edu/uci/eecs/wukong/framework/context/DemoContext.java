package edu.uci.eecs.wukong.framework.context;

public class DemoContext extends BaseContext{
	public int ppnum1;
	public int ppnum2;
	public int ppnum3;
	public int ppnum4;
	public int ppnum5;
	public int ppnum6;
	
	public DemoContext(int ppnum1, int ppnum2, int ppnum3, int ppnum4, int ppnum5, int ppnum6) {
		super("Location");
		this.ppnum1 = ppnum1;
		this.ppnum2 = ppnum2;
		this.ppnum3 = ppnum3;
		this.ppnum4 = ppnum4;
		this.ppnum5 = ppnum5;
		this.ppnum6 = ppnum6;
	}
	
	public DemoContext() {
		super("Location");
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof DemoContext) {
			DemoContext context = (DemoContext) object;
			if(this.ppnum1 == context.ppnum1 && this.ppnum2 == context.ppnum2
					&& this.ppnum3 == context.ppnum3 && this.ppnum4 == context.ppnum4
					&& this.ppnum5 == context.ppnum5 && this.ppnum6 == context.ppnum6) {
				return true;
			}
		}
		return false;
	}
	
	public String toXML() {
		return "";
	}
	
	public String getElementName() {
		return "";
	}
}
