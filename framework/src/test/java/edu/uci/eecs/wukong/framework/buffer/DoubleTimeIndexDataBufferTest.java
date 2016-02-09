package edu.uci.eecs.wukong.framework.buffer;

import junit.framework.TestCase;

import java.util.List;

import edu.uci.eecs.wukong.framework.buffer.BufferUnits.ShortUnit;
import edu.uci.eecs.wukong.framework.model.NPP;

public class DoubleTimeIndexDataBufferTest extends TestCase{
	
	public void testDoubleTimeIndexDataBuffer() {
		DoubleTimeIndexDataBuffer<Short, ShortUnit> buffer =
				new DoubleTimeIndexDataBuffer<Short, ShortUnit>(new NPP(1, (byte)1, (byte)1), ShortUnit.class, 2, 1000, 60, 3);
		for(int i=0; i< 60; i++) {
			buffer.addElement(100000 + 3 * i, new ShortUnit(new Integer(i).shortValue()));
			buffer.addElement(100000 + 3 * i + 1, new ShortUnit(new Integer(i).shortValue()));
			buffer.addElement(100000 + 3 * i + 2, new ShortUnit(new Integer(i).shortValue()));
			buffer.addIndex();	
		}
		
		List<DataPoint<Short>> data = buffer.readDataPoint(3);
		assertEquals(data.size(), 9);
		System.out.println(data.toString());
	}

}
