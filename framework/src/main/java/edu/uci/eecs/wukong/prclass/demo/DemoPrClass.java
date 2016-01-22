package edu.uci.eecs.wukong.prclass.demo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

@WuClass(id = 10000)
public class DemoPrClass extends PipelinePrClass {
	
	public DemoPrClass() {
		super("Demo");
	}
	
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new DemoProgressionExtension(this));
		return extensions;
	}
	
	public List<String> registerContext() {
		List<String> contexts= new ArrayList<String>();
		contexts.add("nooneknow");
		return contexts;
	}
}
