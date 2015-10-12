package edu.uci.eecs.wukong.prclass.timertest;

import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.prclass.timertest.TimerPrClass;

public class TimerTestProgressionExtension extends AbstractProgressionExtension
		implements TimerExecutable {
	private short number = 0; 
	
	public TimerTestProgressionExtension(PrClass prClass) {
		super(prClass);
	}

	public void execute() {
		if (prClass instanceof TimerPrClass) {
			TimerPrClass timerPrClass = (TimerPrClass) prClass;
			timerPrClass.setOuput(number ++);
		}
	}

}
