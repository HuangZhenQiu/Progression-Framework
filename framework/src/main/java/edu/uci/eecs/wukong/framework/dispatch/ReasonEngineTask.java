package edu.uci.eecs.wukong.framework.dispatch;

import edu.uci.eecs.wukong.framework.event.ReasonEvent;

public class ReasonEngineTask<T extends ReasonEvent> extends ProgressionTask{

	private T event;
	
	public ReasonEngineTask(T event) {
		super(new TaskType(TaskType.Type.EXECUTOR));
		this.event = event;
	}
	
	@Override
	public void run() {
		//TODO: huangzhenqiu0825
	}
}
