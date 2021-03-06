package edu.uci.eecs.wukong.framework.buffer;

import java.nio.ByteBuffer;

import edu.uci.eecs.wukong.framework.property.Location;

public class LocationUnit implements BufferUnit<Location> {
	private Location value;
	
	public LocationUnit() {}
	
	public LocationUnit(Location value) {
		this.value = value;
	}

	@Override
	public int size() {
		return 12;
	}

	@Override
	public void parse(ByteBuffer buffer, boolean withSequence) {
		if (withSequence) {
			value = new Location(buffer.getFloat(), buffer.getFloat(), buffer.getFloat(), buffer.getInt());
		} else {
			value = new Location(buffer.getFloat(), buffer.getFloat(), buffer.getFloat());
		}
	}

	@Override
	public Location getValue() {
		return value;
	}

	@Override
	public byte[] toArray() {
		ByteBuffer buffer = ByteBuffer.allocate(size());
		buffer.putFloat(value.getX());
		buffer.putFloat(value.getY());
		buffer.putFloat(value.getZ());
		return buffer.array();
	}

}
