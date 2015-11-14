package edu.uci.eecs.wukong.framework.factor;

import org.jivesoftware.smack.packet.ExtensionElement;

public abstract class BaseFactor implements ExtensionElement {
	private String topicId;
	private String publisher;
	private long timestamp;
	private long lasttime;
	private boolean isTriggered;
	
	public BaseFactor(String topicId) {
		this.topicId = topicId;
		this.isTriggered = false;
	}
	
	public String getTopicId() {
		return topicId;
	}
	
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public long getLasttime() {
		return lasttime;
	}
	public void setLasttime(long lasttime) {
		this.lasttime = lasttime;
	}
	
	public String getNamespace() {
		return "";
	}

	public boolean isTriggered() {
		return isTriggered;
	}

	public void setTriggered(boolean isTriggered) {
		this.isTriggered = isTriggered;
	}
}
