package edu.uci.eecs.wukong.prclass.dtdemo;

import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class DemoPrClass extends PipelinePrClass {

	protected DemoPrClass() {
		super("DemoPrClass");
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
