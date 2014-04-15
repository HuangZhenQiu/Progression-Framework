package edu.uci.eecs.wukong.core.dispatch;

import edu.uci.eecs.wukong.core.event.Event;

public class EventDispatchTask<T extends Event> extends ProgressionTask {

	private T event;
	public EventDispatchTask(T event) {
		super(new TaskType(TaskType.Type.DISPACHER));
		this.event  = event;
		
	}
	
	public void run() {
		//TODO huangzhenqiu0825
	}

}
