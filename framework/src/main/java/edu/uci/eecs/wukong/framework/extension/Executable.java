package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;

public interface Executable<T extends FeatureEntity> {
	public void execute(List<T> data, ExecutionContext context);
}
