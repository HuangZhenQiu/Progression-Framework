package edu.uci.eecs.wukong.framework.entity;

import edu.uci.eecs.wukong.framework.prclass.PrClass;

public abstract class Entity {
	private final PrClass prClass;
	
	public Entity (PrClass prClass) {
		this.prClass = prClass;
	}
	
	public PrClass getPrClass() {
		return this.prClass;
	}
}
