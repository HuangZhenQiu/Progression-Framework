package edu.uci.eecs.wukong.plugin.demo;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.Plugin;
import edu.uci.eecs.wukong.framework.extension.Extension;

public class DemoPlugin extends Plugin {
	
	public DemoPlugin() {
		super("Demo");
	}
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new Demo2ProgressionExtension());
		return extensions;
	}
	
	public List<String> registerContext() {
		List<String> contexts= new ArrayList<String>();
		contexts.add("nooneknow");
		return contexts;
	}
}
