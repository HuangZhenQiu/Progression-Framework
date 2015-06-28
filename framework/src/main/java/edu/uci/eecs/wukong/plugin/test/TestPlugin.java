package edu.uci.eecs.wukong.plugin.test;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.Output;
import edu.uci.eecs.wukong.framework.annotation.WuClassID;
import edu.uci.eecs.wukong.framework.extension.Extension;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

@WuClassID(number = 1002)
public class TestPlugin extends Plugin {

	@Output
	private String testProperty;
	
	public TestPlugin(String appId) {
		super(appId, "Test");
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

	public String getTestProperty() {
		return testProperty;
	}

	public void setTestProperty(String testProperty) {
		String oldValue = this.testProperty;
		this.testProperty = testProperty;
		this.support.fireIndexedPropertyChange("testProperty", 1, oldValue, testProperty);
	}
}
