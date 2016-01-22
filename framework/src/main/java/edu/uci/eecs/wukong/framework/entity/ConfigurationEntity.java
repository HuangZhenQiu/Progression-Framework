package edu.uci.eecs.wukong.framework.entity;

import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class ConfigurationEntity extends Entity{
	private String componentId;
	private int value;
	
	public ConfigurationEntity(PipelinePrClass prClass, String componentId, int value) {
		super(prClass);
		this.componentId = componentId;
		this.value = value;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}
