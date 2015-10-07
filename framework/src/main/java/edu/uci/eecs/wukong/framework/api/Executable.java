package edu.uci.eecs.wukong.framework.api;

import java.util.List;

import edu.uci.eecs.wukong.framework.entity.FeatureEntity;

public interface Executable<T extends FeatureEntity> {
	public void execute(List<T> data, ExecutionContext context);
}
