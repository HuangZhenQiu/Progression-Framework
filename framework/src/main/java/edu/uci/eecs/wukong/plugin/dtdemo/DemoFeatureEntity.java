package edu.uci.eecs.wukong.plugin.dtdemo;

import edu.uci.eecs.wukong.framework.entity.FeatureEntity;

public class DemoFeatureEntity implements FeatureEntity {
	private Short value;
	
	public DemoFeatureEntity(Short value) {
		this.value = value;
	}
	
	public Short getValue() {
		return value;
	}
	public void setValue(Short t) {
		this.value = t;
	}
}
