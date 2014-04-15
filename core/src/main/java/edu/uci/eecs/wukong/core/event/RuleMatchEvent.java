package edu.uci.eecs.wukong.core.event;

public class RuleMatchEvent extends Event {

	public static int PRIORITY = 99;
	
	public RuleMatchEvent() {
		super(PRIORITY);
	}
}
