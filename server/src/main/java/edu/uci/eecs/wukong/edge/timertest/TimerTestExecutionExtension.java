package edu.uci.eecs.wukong.edge.timertest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.edge.timertest.TimerEdgeClass;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;

public class TimerTestExecutionExtension extends AbstractExecutionExtension
		implements TimerExecutable {
	private static Logger logger = LoggerFactory.getLogger(TimerTestExecutionExtension.class);
	private short number = 0; 
	
	public TimerTestExecutionExtension(EdgePrClass prClass) {
		super(prClass);
	}

	public void execute() {
		if (prClass instanceof TimerEdgeClass) {
			TimerEdgeClass timerPrClass = (TimerEdgeClass) prClass;
			logger.info("Update output to value " + number);
			timerPrClass.setOutput(number ++);
		}
	}

}
