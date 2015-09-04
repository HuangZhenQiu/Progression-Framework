package edu.uci.eecs.wukong.plugin.demo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuClassID;
import edu.uci.eecs.wukong.framework.extension.Extension;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

@WuClassID(number = 10000)
public class DemoPlugin extends Plugin {
	
	public DemoPlugin(String appId) {
		super(appId, "Demo");
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
