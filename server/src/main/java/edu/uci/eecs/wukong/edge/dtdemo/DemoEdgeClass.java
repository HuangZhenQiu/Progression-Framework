package edu.uci.eecs.wukong.edge.dtdemo;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

public class DemoEdgeClass extends EdgePrClass {

	protected DemoEdgeClass(PrClassMetrics metrics) {
		super("DemoPrClass", metrics);
	}

	@Override
	public List<Extension> registerExtension() {
		return null;
	}

	@Override
	public List<String> registerContext() {
		return null;
	}

}
