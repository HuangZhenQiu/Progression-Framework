package edu.uci.eecs.wukong.edge.timertest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.edge.timertest.TimerPrClass;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class TimerTestProgressionExtension extends AbstractProgressionExtension
		implements TimerExecutable {
	private static Logger logger = LoggerFactory.getLogger(TimerTestProgressionExtension.class);
	private short number = 0; 
	
	public TimerTestProgressionExtension(PipelinePrClass prClass) {
		super(prClass);
	}

	public void execute() {
		if (prClass instanceof TimerPrClass) {
			TimerPrClass timerPrClass = (TimerPrClass) prClass;
			logger.info("Update output to value " + number);
			timerPrClass.setOutput(number ++);
		}
	}

}
