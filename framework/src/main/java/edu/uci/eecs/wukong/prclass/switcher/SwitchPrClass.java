package edu.uci.eecs.wukong.prclass.switcher;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;


@WuClass(id = 10001)
public class SwitchPrClass extends PipelinePrClass {
	
	@WuProperty(id = 0, type = PropertyType.Input, dtype = DataType.Channel)
	private Boolean switchInput;
	@WuProperty(id = 1, type = PropertyType.Input, dtype = DataType.Buffer)
	private Double temparature;
	@WuProperty(id = 2, type = PropertyType.Output, dtype = DataType.Short)
	private Double threshold;
	
	public SwitchPrClass() {
		super("SwitchPlugin");
	}
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new SwitchFeatureExtensionExtension(this));
		extensions.add(new SwitchLearningExtension(this));
		extensions.add(new SwitchProgressionExtension(this));
		return extensions;
	}
	
	public List<String> registerContext() {
		List<String> contexts= new ArrayList<String>();
		contexts.add("uid");
		return contexts;
	}
	
	public Boolean getSwitchInput() {
		return switchInput;
	}
	public void setSwitchInput(Boolean switchInput) {
		this.switchInput = switchInput;
	}
	public Double getTemparature() {
		return temparature;
	}
	public void setTemparature(Double temparature) {
		this.temparature = temparature;
	}
	public Double getThreshold() {
		return threshold;
	}
	public void setThreshold(Double threshold) {
		Double oldValue = this.threshold;
		this.threshold = threshold;
		this.support.fireIndexedPropertyChange("threshold", 1, oldValue, threshold);
	}
}
