package edu.uci.eecs.wukong.framework.entity;

import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public abstract class Entity {
	private final PipelinePrClass prClass;
	
	public Entity (PipelinePrClass prClass) {
		this.prClass = prClass;
	}
	
	public PipelinePrClass getPrClass() {
		return this.prClass;
	}
}
