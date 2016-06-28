package edu.uci.eecs.wukong.framework.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.SensorData;
import edu.uci.eecs.wukong.framework.model.SensorType;

public class SystemDoubleTimeIndexDataBuffer extends DoubleTimeIndexDataBuffer<SensorData, SensorDataUnit> {
	private static Logger logger = LoggerFactory.getLogger(SystemDoubleTimeIndexDataBuffer.class);
	private SensorType key;
	
	public SystemDoubleTimeIndexDataBuffer(SensorType key, Class<SensorDataUnit> type, int unitSize, int dataCapacity, int timeUnits, int interval) {
		super(type, unitSize, dataCapacity, timeUnits, interval);
		this.key = key;
	}

	@Override
	public String getKey() {
		return key.name();
	}

	@Override
	public List<DataPoint<SensorData>> readDataPoint(int units) {
		ByteBuffer data = read(units);
		int size = data.capacity() / dataBuffer.getUnitLength();
		List<DataPoint<SensorData>> points = new ArrayList<DataPoint<SensorData>>();
		while(size > 0) {
			try {
				SensorDataUnit unit = (SensorDataUnit)type.getConstructor().newInstance();
				int timestamp = data.getInt();
				unit.parse(data, false);
				points.add(new DataPoint<SensorData>(unit.getValue().getNPP(), timestamp, unit.getValue()));
			} catch (Exception e) {
				logger.error("Error in reading data point from buffer " + getKey() + e.toString());
			}
			size --;
		}
		
		return points;
	}

}
