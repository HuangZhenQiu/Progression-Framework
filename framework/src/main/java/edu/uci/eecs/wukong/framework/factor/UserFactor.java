package edu.uci.eecs.wukong.framework.factor;

public class UserFactor extends BaseFactor {
	private String uid;
	
	public UserFactor(String uid) {
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
