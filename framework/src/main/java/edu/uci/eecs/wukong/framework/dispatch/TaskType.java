package edu.uci.eecs.wukong.framework.dispatch;

public class TaskType {
	public static enum Type {APPENDER, DISPACHER, MATCHER, EXECUTOR};
	
	private Type type;
	
	public TaskType(TaskType.Type type){
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public boolean equals(TaskType type){
		return this.type == type.type;
	}
}
