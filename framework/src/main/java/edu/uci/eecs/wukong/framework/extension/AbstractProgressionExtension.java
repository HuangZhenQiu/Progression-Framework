package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public abstract class AbstractProgressionExtension<T extends FeatureEntity>
	extends AbstractExtension {
	
	public AbstractProgressionExtension(Plugin plugin) {
		super(plugin);
	}
}
