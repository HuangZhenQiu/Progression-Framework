package edu.uci.eecs.wukong.framework.entity;

import java.util.List;
import java.util.ArrayList;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
/**
 * It is the element that is stored in feature store
 * @author Peter Huang
 *
 */
public class FeatureEntity<T> extends Entity {
	public List<T> features;
	
	public FeatureEntity(PrClass prClass) {
		super(prClass);
		this.features = new ArrayList<T> ();
	}
	
	public List<T> getFeatures() {
		return this.features;
	}
 }
