package edu.uci.eecs.wukong.framework.entity;

import java.util.List;
import java.util.ArrayList;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
/**
 * It is the element that is stored in feature store
 * @author Peter Huang
 *
 */
public class FeatureEntity<T> extends Entity {
	public List<T> features;
	
	public FeatureEntity(EdgePrClass prClass) {
		super(prClass);
		this.features = new ArrayList<T> ();
	}
	
	public void addFeatures(List<T> list) {
		features.addAll(list);
	}
	
	public List<T> getFeatures() {
		return this.features;
	}
 }
