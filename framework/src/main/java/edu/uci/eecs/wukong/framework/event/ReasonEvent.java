package edu.uci.eecs.wukong.framework.event;

public class ReasonEvent extends Event{
	
	public static int PRIORITY = 100;
	
	public ReasonEvent() {
		super(PRIORITY);
	}
}
