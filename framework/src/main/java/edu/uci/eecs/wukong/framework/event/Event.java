package edu.uci.eecs.wukong.framework.event;

public class Event implements Comparable<Event>, IEvent{
	
	private int priority;
	
	public Event(int priority) {
		this.priority = priority;
	}

	public int compareTo(Event o){
		return this.priority >= o.priority ? 1 : -1;
	}
}
