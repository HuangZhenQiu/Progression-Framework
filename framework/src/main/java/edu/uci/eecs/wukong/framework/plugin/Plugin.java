package edu.uci.eecs.wukong.framework.plugin;

import java.util.List;
import edu.uci.eecs.wukong.framework.extension.Extension;

public abstract class Plugin {
	private String name;
	public Plugin(String name) {
		this.name = name;
	}

	public abstract List<Extension> registerExtension();
	
	public abstract List<String> registerContext();
	
	public String getName() {
		return name;
	}
}
