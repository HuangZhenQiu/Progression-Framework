package edu.uci.eecs.wukong.edge.test;

import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPropertyProgressionExtension extends AbstractProgressionExtension 
	implements TimerExecutable {
	private static Logger logger = LoggerFactory.getLogger(TestPropertyProgressionExtension.class);

	public TestPropertyProgressionExtension(PipelinePrClass plugin) {
		super(plugin);
	}

	public void execute() {
		if(this.prClass instanceof TestPrClass) {
			((TestPrClass) prClass).setTestProperty((short)UUID.randomUUID().getLeastSignificantBits());
			logger.info("TestPropertyProgressionExtension updated proerpty: " + ((TestPrClass) prClass).getTestProperty());
		}
	}

}
