package edu.uci.eecs.wukong.core.dispatch;

import edu.uci.eecs.wukong.core.event.RuleMatchEvent;

public class JessRuleMatchTask<T extends RuleMatchEvent> extends ProgressionTask {

	private T event;
	
	public JessRuleMatchTask(T event) {
		super(new TaskType(TaskType.Type.MATCHER));
		this.event = event;
	}
	
	@Override
	public void run() {
		//TODO: huangzhenqiu0825
	}
}
