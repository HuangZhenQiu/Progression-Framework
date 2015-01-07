package edu.uci.eecs.wukong.framework.context;

import org.jivesoftware.smack.packet.PacketExtension;

public abstract class Context implements PacketExtension {
	private String publisher;
	private String topicId;
	private long timestamp;
	private long lasttime;
	
	public Context(String topicId) {
		this.topicId = topicId;
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
}
