package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public abstract class AbstratProgressionExtension<T extends FeatureEntity> implements PropertyProgressionExtension<T> {
	
	private Plugin plugin;
	
	public AbstratProgressionExtension(Plugin plugin) {
		this.plugin = plugin;
	}

	public abstract void activate(Object model);

	public abstract void execute(List<T> data, ExecutionContext context);

	public abstract void execute(Context context);

	public abstract void execute();
	
	public Plugin getPlugin() {
		return this.plugin;
	}
}