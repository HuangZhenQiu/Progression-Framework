package edu.uci.eecs.wukong.framework.event;

import java.util.List;

public class Event<T extends Number> implements Comparable<Event>, IEvent<T>{
	private String appId;
	private List<T> data;
	private int priority;
	
	public Event(int priority, String appId, List<T> data) {
		this.priority = priority;
		this.appId = appId;
		this.data = data;
	}

	public int compareTo(Event o){
		return this.priority >= o.priority ? 1 : -1;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public List<T> getData() {
		// TODO Auto-generated method stub
		return data;
	}
	
	public String getAppId() {
		return this.appId;
	}
}
