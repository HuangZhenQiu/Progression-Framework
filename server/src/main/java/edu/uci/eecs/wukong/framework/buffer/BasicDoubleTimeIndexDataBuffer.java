package edu.uci.eecs.wukong.framework.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.NPP;

public class BasicDoubleTimeIndexDataBuffer<T, E extends BufferUnit<T>> extends DoubleTimeIndexDataBuffer<T, E> {
	private static Logger logger = LoggerFactory.getLogger(BasicDoubleTimeIndexDataBuffer.class);
	private NPP npp;
	
	public BasicDoubleTimeIndexDataBuffer(NPP npp, Class<E> type, int unitSize, int dataCapacity, int timeUnits,
			int interval) {
		super(type, unitSize, dataCapacity, timeUnits, interval);
		this.npp = npp;
	}

	public List<DataPoint<T>> readDataPoint(int units) {
		ByteBuffer data = read(units);
		int size = data.capacity() / dataBuffer.getUnitLength();
		List<DataPoint<T>> points = new ArrayList<DataPoint<T>>();
		while(size > 0) {
			try {
				BufferUnit<T> unit = (BufferUnit<T>)type.getConstructor().newInstance();
				int timestamp = data.getInt();
				unit.parse(data, false);
				points.add(new DataPoint<T>(npp, timestamp, unit.getValue()));
			} catch (Exception e) {
				logger.error("Error in reading data point from buffer " + getKey() + e.toString());
			}
			size --;
		}
		
		return points;
	}

	@Override
	public String getKey() {
		return this.npp.toString();
	}
}
