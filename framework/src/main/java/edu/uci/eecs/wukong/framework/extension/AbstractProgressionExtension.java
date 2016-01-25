package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public abstract class AbstractProgressionExtension<T extends PipelinePrClass>
	extends AbstractExtension<T> {
	
	public AbstractProgressionExtension(T plugin) {
		super(plugin);
	}
	
	public boolean isSubcribedTopic(String topic) {
		return this.getPrClass().registerContext().contains(topic);
	}
}
