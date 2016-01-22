package edu.uci.eecs.wukong.prclass.dtdemo;

import java.util.ArrayList;
import java.util.List;

import weka.classifiers.trees.M5P;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class DemoLearningExtension extends LearningExtension<Number> {
	private int attributeNumber;
	private FastVector<Attribute> attributes;
	private Instances traniningSet;
	private M5P tree;
	
	public DemoLearningExtension(PipelinePrClass plugin) {
		super(plugin);
		this.attributeNumber = 7; // Context + Light Sensor
		this.attributes = new FastVector<Attribute>(7);
		this.tree = new M5P();
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
	public void apply(List<Number> data,
			ExecutionContext context) {
		Instance sample = new DenseInstance(attributeNumber);
		int i = 0;
		for (Number entity : data) {
			sample.setValue(attributes.get(i), entity.doubleValue());
			i++;
		}
		this.traniningSet.add(sample);
	}

	public Object train() throws Exception{
		tree.buildClassifier(this.traniningSet);
		return tree;
	}
	
	public List<Double> predict(List<Number> data,
			ExecutionContext context) throws Exception {
		Instance observation = new DenseInstance(attributeNumber);
		// Use context to build observation
		List<Double> result = new ArrayList<Double>();
		result.add(tree.classifyInstance(observation));
		
		return result;
	}

}
