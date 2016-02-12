package edu.uci.eecs.wukong.framework.buffer;

import java.nio.ByteBuffer;

import edu.uci.eecs.wukong.framework.property.Activity;

public class ActivityUnit implements BufferUnit<Activity> {
	public Activity value;
	
	public ActivityUnit() {}
	
	public ActivityUnit(Activity value) {
		this.value = value;
	}

	@Override
	public void parse(ByteBuffer buffer) {
		value = new Activity(buffer.getLong(), buffer.getShort(), buffer.getFloat());
	}

	@Override
	public Activity getValue() {
		return value;
	}

	@Override
	public int size() {
		return 14;
	}

	@Override
	public byte[] toArray() {
		ByteBuffer buffer = ByteBuffer.allocate(size());
		buffer.putLong(value.getTimeStamp());
		buffer.putShort(value.getDeviceId());
		buffer.putFloat(value.getValue());
		return buffer.array();
	}

}
