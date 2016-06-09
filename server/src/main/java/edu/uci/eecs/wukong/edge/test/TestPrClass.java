package edu.uci.eecs.wukong.edge.test;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;

@WuClass(id = 10002)
public class TestPrClass extends PipelinePrClass {

	@WuProperty(id = 0, type = PropertyType.Input, dtype = DataType.Buffer)
	private short testProperty;
	
	public TestPrClass(PrClassMetrics metrics) {
		super("Test", metrics);
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new TestPropertyExecutionExtension(this));
		return extensions;
	}

	@Override
	public List<String> registerContext() {
		return new ArrayList<String>();
	}

	public short getTestProperty() {
		return testProperty;
	}

	public void setTestProperty(short testProperty) {
		short oldValue = this.testProperty;
		this.testProperty = testProperty;
		this.support.fireIndexedPropertyChange("testProperty", 1, oldValue, testProperty);
	}
}
