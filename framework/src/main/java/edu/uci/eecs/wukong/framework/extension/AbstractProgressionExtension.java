package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public abstract class AbstractProgressionExtension
	extends AbstractExtension {
	
	public AbstractProgressionExtension(PipelinePrClass plugin) {
		super(plugin);
	}
	
	public boolean isSubcribedTopic(String topic) {
		return this.getPrClass().registerContext().contains(topic);
	}
}
