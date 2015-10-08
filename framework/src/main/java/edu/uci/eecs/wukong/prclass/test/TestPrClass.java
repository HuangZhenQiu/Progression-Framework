package edu.uci.eecs.wukong.prclass.test;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

@WuClass(id = 10002)
public class TestPrClass extends PrClass {

	@WuProperty(id = 1, type = PropertyType.Input, dtype = DataType.Buffer)
	private short testProperty;
	
	public TestPrClass() {
		super("Test");
	}

	@Override
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new TestPropertyProgressionExtension(this));
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