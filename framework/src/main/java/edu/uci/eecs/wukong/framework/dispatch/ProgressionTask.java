package edu.uci.eecs.wukong.framework.dispatch;


public abstract class ProgressionTask implements Runnable{
	
	private TaskType type;
	
	public ProgressionTask(TaskType type) {
		this.type = type;
	}
	
	//subclass should override the function
	public abstract void run();
}
