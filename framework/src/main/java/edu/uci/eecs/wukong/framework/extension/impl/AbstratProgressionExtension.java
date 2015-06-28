package edu.uci.eecs.wukong.framework.extension.impl;

import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public abstract class AbstratProgressionExtension<T extends FeatureEntity>
	extends AbstractExtension {
	
	public AbstratProgressionExtension(Plugin plugin) {
		super(plugin);
	}
}
