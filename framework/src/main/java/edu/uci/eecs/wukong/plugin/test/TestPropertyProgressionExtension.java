package edu.uci.eecs.wukong.plugin.test;

import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPropertyProgressionExtension extends AbstractProgressionExtension 
	implements TimerExecutable {
	private static Logger logger = LoggerFactory.getLogger(TestPropertyProgressionExtension.class);

	public TestPropertyProgressionExtension(Plugin plugin) {
		super(plugin);
	}

	public void execute() {
		if(this.plugin instanceof TestPlugin) {
			((TestPlugin) plugin).setTestProperty((short)UUID.randomUUID().getLeastSignificantBits());
			logger.info("TestPropertyProgressionExtension updated proerpty: " + ((TestPlugin) plugin).getTestProperty());
		}
	}

}
