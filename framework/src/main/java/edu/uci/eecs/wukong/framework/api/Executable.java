package edu.uci.eecs.wukong.framework.api;

import java.util.List;

public interface Executable<T extends Number> {
	public void execute(List<T> data, ExecutionContext context);
}
