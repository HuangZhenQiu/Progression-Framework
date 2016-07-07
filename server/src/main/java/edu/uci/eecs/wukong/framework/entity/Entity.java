package edu.uci.eecs.wukong.framework.entity;

import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;

public abstract class Entity {
	private final EdgePrClass prClass;
	
	public Entity (EdgePrClass prClass) {
		this.prClass = prClass;
	}
	
	public EdgePrClass getPrClass() {
		return this.prClass;
	}
}
