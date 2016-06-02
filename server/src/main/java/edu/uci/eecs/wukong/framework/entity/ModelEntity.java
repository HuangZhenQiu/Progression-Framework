package edu.uci.eecs.wukong.framework.entity;

import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class ModelEntity extends Entity {
	private Object model;
	public ModelEntity(PipelinePrClass prClass, Object model) {
		super(prClass);
		this.model = model;
	}
	
	public Object getModel() {
		return this.model;
	}
}
