package edu.uci.eecs.wukong.edge.demo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

@WuClass(id = 10000)
public class DemoPrClass extends PipelinePrClass {
	
	public DemoPrClass(PrClassMetrics metrics) {
		super("Demo", metrics);
	}
	
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new DemoExecutionExtension(this));
		return extensions;
	}
	
	public List<String> registerContext() {
		List<String> contexts= new ArrayList<String>();
		contexts.add("nooneknow");
		return contexts;
	}
}
