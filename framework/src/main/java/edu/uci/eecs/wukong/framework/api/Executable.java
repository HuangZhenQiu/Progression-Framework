package edu.uci.eecs.wukong.framework.api;

import java.util.List;

public interface Executable {
	public void execute(List data, ExecutionContext context);
}
