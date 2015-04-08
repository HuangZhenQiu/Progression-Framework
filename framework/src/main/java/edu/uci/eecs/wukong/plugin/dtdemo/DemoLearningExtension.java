package edu.uci.eecs.wukong.plugin.dtdemo;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;

public class DemoLearningExtension implements LearningExtension<DemoFeatureEntity> {
	private int attributeNumber;
	private FastVector<Attribute> attributes;
	private Instances traniningSet;
	private M5P tree;
	private boolean isActive;
	
	public DemoLearningExtension() {
		this.attributeNumber = 7; // Context + Light Sensor
		this.attributes = new FastVector<Attribute>(7);
		this.tree = new MSP();
		this.isActive = false;
		Attribute light1 = new Attribute("light1");
		Attribute light2 = new Attribute("light2");
		Attribute light3 = new Attribute("light3");
		Attribute light4 = new Attribute("light4");
		Attribute location = new Attribute("location");
		Attribute person = new Attribute("person");
		Attribute posture = new Attribute("posture");
		attributes.add(light1);
		attributes.add(light2);
		attributes.add(light3);
		attributes.add(light4);
		attributes.add(location);
		attributes.add(person);
		attributes.add(posture);
		this.traniningSet = new Instances("Rel", attributes, 10);
		
	}
	public void apply(List<DemoFeatureEntity> data,
			ExecutionContext context) {
		Instance sample = new DenseInstance(attributeNumber);
		int i = 0;
		for (DemoFeatureEntity entity : data) {
			sample.setValue(attributes.get(i), entity.getValue());
			i++;
		}
		this.traniningSet.add(sample);
	}

	public boolean train() throws Exception{
		tree.buildClassifier(this.traniningSet);
		this.isActive = true;
		return this.isActive;
	}
	
	public List<Double> predict(List<DemoFeatureEntity> data,
			ExecutionContext context) throws Exception {
		Instance observation = new DenseInstance(attributeNumber);
		// Use context to build observation
		List<Double> result = new ArrayList<Double>();
		result.add(tree.classifyInstance(observation));
		
		return result;
	}

}
