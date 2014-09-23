package edu.uci.eecs.wukong.framework.event;

public class RuleMatchEvent extends Event {

	public static int PRIORITY = 99;
	
	public RuleMatchEvent() {
		super(PRIORITY);
	}
}
