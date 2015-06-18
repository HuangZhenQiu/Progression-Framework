package edu.uci.eecs.wukong.framework.context;

public class UserContext extends Context {
	private String uid;
	
	public UserContext(String uid) {
		super("uid");
		this.uid = uid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toXML() {
		// TODO Auto-generated method stub
		return null;
	}
}
