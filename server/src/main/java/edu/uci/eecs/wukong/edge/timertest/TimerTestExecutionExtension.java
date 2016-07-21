package edu.uci.eecs.wukong.edge.timertest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.edge.timertest.TimerEdgeClass;
import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;

public class TimerTestExecutionExtension extends AbstractExecutionExtension<TimerEdgeClass>
		implements TimerExecutable {
	private static Logger logger = LoggerFactory.getLogger(TimerTestExecutionExtension.class);
	
	public TimerTestExecutionExtension(TimerEdgeClass prClass) {
		super(prClass);
	}

	@WuTimer(interval = 5)
	public void execute() {
		if (prClass instanceof TimerEdgeClass) {
			TimerEdgeClass timerPrClass = (TimerEdgeClass) prClass;
			if (((TimerEdgeClass) prClass).getOutput()) {
				logger.info("Update output to value false");
				timerPrClass.setOutput(false);
			} else {
				logger.info("Update output to value true");
				timerPrClass.setOutput(true);
			}
		}
	}

}
