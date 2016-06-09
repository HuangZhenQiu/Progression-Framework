package edu.uci.eecs.wukong.edge.test;

import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPropertyExecutionExtension extends AbstractExecutionExtension 
	implements TimerExecutable {
	private static Logger logger = LoggerFactory.getLogger(TestPropertyExecutionExtension.class);

	public TestPropertyExecutionExtension(PipelinePrClass plugin) {
		super(plugin);
	}

	public void execute() {
		if(this.prClass instanceof TestPrClass) {
			((TestPrClass) prClass).setTestProperty((short)UUID.randomUUID().getLeastSignificantBits());
			logger.info("TestPropertyProgressionExtension updated proerpty: " + ((TestPrClass) prClass).getTestProperty());
		}
	}

}
