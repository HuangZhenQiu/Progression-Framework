package edu.uci.eecs.wukong.plugin.test;

import edu.uci.eecs.wukong.framework.extension.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.impl.AbstratProgressionExtension;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

import java.util.UUID;

public class TestPropertyProgressionExtension extends AbstratProgressionExtension 
	implements TimerExecutable {

	public TestPropertyProgressionExtension(Plugin plugin) {
		super(plugin);
	}

	public void execute() {
		if(this.plugin instanceof TestPlugin) {
			((TestPlugin) plugin).setTestProperty(UUID.randomUUID().toString());
		}
	}

}
