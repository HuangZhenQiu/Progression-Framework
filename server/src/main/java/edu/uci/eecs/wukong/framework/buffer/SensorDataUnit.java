package edu.uci.eecs.wukong.framework.buffer;

import java.nio.ByteBuffer;

import edu.uci.eecs.wukong.framework.model.SensorData;

/**
 * It is a buffer unit to keep a category of sensor data. Since the set of data 
 * belongs to a category of sensor in the system, we need to record the NPP value
 * of each data point.
 * 
 * 
 * @author peter
 *
 */
public class SensorDataUnit implements BufferUnit<SensorData> {
	private SensorData value;
	
	public SensorDataUnit(SensorData value) {
		this.value = value;
	}

	@Override
	public void parse(ByteBuffer buffer, boolean withSequence) {
		value = new SensorData(buffer.getInt(), buffer.get(), buffer.get(), buffer.getShort());
	}

	@Override
	public SensorData getValue() {
		return value;
	}

	@Override
	public int size() {
		return 8;
	}

	@Override
	public byte[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

}
