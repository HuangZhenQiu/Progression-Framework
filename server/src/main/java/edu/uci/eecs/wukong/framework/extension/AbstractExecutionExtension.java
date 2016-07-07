package edu.uci.eecs.wukong.framework.extension;

import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;

public abstract class AbstractExecutionExtension<T extends EdgePrClass>
	extends AbstractExtension<T> {
	public static final String PROGRESISON_PREFIX = "progression";
	
	public AbstractExecutionExtension(T plugin) {
		super(plugin);
	}
	
	public boolean isSubcribedTopic(String topic) {
		return this.getPrClass().registerContext().contains(topic);
	}
	
	@Override
	public String prefix() {
		return PROGRESISON_PREFIX;
	}
}
