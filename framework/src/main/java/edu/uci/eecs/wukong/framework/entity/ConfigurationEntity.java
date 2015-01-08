package edu.uci.eecs.wukong.framework.entity;

public class ConfigurationEntity {
	private String componentId;
	private int value;
	
	public ConfigurationEntity(String componentId, int value) {
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
