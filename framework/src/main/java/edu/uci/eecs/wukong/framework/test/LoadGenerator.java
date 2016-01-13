package edu.uci.eecs.wukong.framework.test;

import java.lang.Number;
import java.util.TimerTask;

/**
 * Simulate a sensor to periodically send value to progression server as workload.
 * 
 */
public abstract class LoadGenerator<T extends Number> extends TimerTask {
	private short wuclassId;
	private byte port;
	private byte propertyId;
	private WKPFMessageSender sender;
	private Class<T> type;
	
	public LoadGenerator(short wuclassId, byte port, byte propertyId, Class<T> type) {
		this.wuclassId = wuclassId;
		this.port = port;
		this.propertyId = propertyId;
		this.type = type;
	}
	
	public abstract T nextValue();
	
	public void setSender(WKPFMessageSender sender) {
		this.sender = sender;
	}

	@Override
	public void run() {
		if (sender != null) {
			Number value = nextValue();
			if (type.isInstance(Byte.class) || type.isInstance(Boolean.class)) {

				sender.sendWriteByteProperty(port, wuclassId, propertyId, (byte)value);
			} else {
				sender.sendWriteShortProperty(port, wuclassId, propertyId, (short)value);
			}
		}
	}
}
