package edu.uci.eecs.wukong.prclass.test;

import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPropertyProgressionExtension extends AbstractProgressionExtension 
	implements TimerExecutable {
	private static Logger logger = LoggerFactory.getLogger(TestPropertyProgressionExtension.class);

	public TestPropertyProgressionExtension(PrClass plugin) {
		super(plugin);
	}

	public void execute() {
		if(this.plugin instanceof TestPrClass) {
			((TestPrClass) plugin).setTestProperty((short)UUID.randomUUID().getLeastSignificantBits());
			logger.info("TestPropertyProgressionExtension updated proerpty: " + ((TestPrClass) plugin).getTestProperty());
		}
	}

}