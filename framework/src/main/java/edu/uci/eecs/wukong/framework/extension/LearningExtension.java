package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.ConfigurationCommand;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;

public interface LearningExtension<T extends FeatureEntity> extends Extension<T> {

	public List<ConfigurationCommand> apply(List<T> data, ExecutionContext context);
	
	public Object train();
}
