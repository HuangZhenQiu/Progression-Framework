package edu.uci.eecs.wukong.prclass.dtdemo;

import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class DemoFeatureEntity extends FeatureEntity<Short> {
	private Short value;
	
	public DemoFeatureEntity(PrClass prClass, Short value) {
		super(prClass);
		this.features.add(value);
	}
	
	public Short getValue() {
		return value;
	}
	public void setValue(Short t) {
		this.value = t;
	}
}
