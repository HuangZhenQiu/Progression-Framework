package edu.uci.eecs.wukong.plugin.switcher;

import java.util.ArrayList;
import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.Input;
import edu.uci.eecs.wukong.framework.annotation.Output;
import edu.uci.eecs.wukong.framework.extension.Extension;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

public class SwitchPlugin extends Plugin {
	
	@Input(interval = 5)
	private String input;
	@Output
	private String threshold;
	
	public SwitchPlugin() {
		super("Smart Switch");
	}
	public List<Extension> registerExtension() {
		List<Extension> extensions = new ArrayList<Extension>();
		extensions.add(new SwitchProgressionExtension());
		return extensions;
	}
	
	public List<String> registerContext() {
		List<String> contexts= new ArrayList<String>();
		contexts.add("uid");
		return contexts;
	}
	
}
