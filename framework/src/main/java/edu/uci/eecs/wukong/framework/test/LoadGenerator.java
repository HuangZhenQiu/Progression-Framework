package edu.uci.eecs.wukong.framework.test;

import java.lang.Number;
import java.util.Random;
import java.util.TimerTask;

/**
 * Simulate a sensor to periodically send value to progression server as workload.
 * 
 */
public abstract class LoadGenerator<T extends Number> extends TimerTask {
	private short wuclassId;
	private byte port;
	private byte propertyId;
	private boolean collect;
	private WKPFMessageSender sender;
	protected Class<T> type;
	
	public LoadGenerator(short wuclassId, byte port, byte propertyId, Class<T> type, boolean collect) {
		this.wuclassId = wuclassId;
		this.port = port;
		this.propertyId = propertyId;
		this.type = type;
		this.collect = collect;
	}
	
	public abstract T nextValue();
	
	public void setSender(WKPFMessageSender sender) {
		this.sender = sender;
	}

	@Override
	public void run() {
		if (sender != null) {
			Number value = nextValue();
			if (type.isAssignableFrom(Byte.class) || type.isAssignableFrom(Boolean.class)) {
				sender.sendWriteByteProperty(port, wuclassId, propertyId, (byte)value, collect);
			} else {
				sender.sendWriteShortProperty(port, wuclassId, propertyId, (short)value, collect);
			}
		}
	}

	public static class RandomByteGenerator extends LoadGenerator<Byte> {
		private Random random;
		public RandomByteGenerator(short wuclassId, byte port, byte propertyId, boolean collect) {
			super(wuclassId, port, propertyId, Byte.class, collect);
		}

		@Override
		public Byte nextValue() {
			return random.nextBoolean() == true ? (byte) 1 : (byte) 0;
		}
	}
	
	public static class RandomShortGenerator extends LoadGenerator<Short> {
		private Random random;
		public RandomShortGenerator(short wuclassId, byte port, byte propertyId, boolean collect) {
			super(wuclassId, port, propertyId, Short.class, collect);
		}

		@Override
		public Short nextValue() {
			return (short) (random.nextInt() % Short.MAX_VALUE);
		}
	}
}
