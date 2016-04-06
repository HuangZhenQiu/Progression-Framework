package edu.uci.eecs.wukong.prclass.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.annotation.WuClass;
import edu.uci.eecs.wukong.framework.annotation.WuProperty;
import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.model.DataType;
import edu.uci.eecs.wukong.framework.model.PropertyType;
import edu.uci.eecs.wukong.framework.prclass.PrClassMetrics;
import edu.uci.eecs.wukong.framework.prclass.SimplePrClass;

@WuClass(id = 10114)
public class SimpleInputPrClass extends SimplePrClass {
	private final static Logger LOGGER = LoggerFactory.getLogger(SimpleInputPrClass.class);
	@WuProperty(id = 0, name="input", type = PropertyType.Input, dtype = DataType.Channel)
	private short input;
	
	public SimpleInputPrClass(PrClassMetrics metrics) {
		super("SimpleInputPrClass", metrics);
		// TODO Auto-generated constructor stub
	}

	@Override
	@WuTimer(interval = 0.1F)
	public void update() {
		LOGGER.info("Simple Input PrClass current input value = " + input);
	}
}
