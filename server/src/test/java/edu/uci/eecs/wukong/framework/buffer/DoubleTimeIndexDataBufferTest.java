package edu.uci.eecs.wukong.framework.buffer;

import junit.framework.TestCase;

import java.util.List;

import org.junit.Test;

import edu.uci.eecs.wukong.framework.buffer.BufferUnits.ShortUnit;
import edu.uci.eecs.wukong.framework.model.NPP;

public class DoubleTimeIndexDataBufferTest extends TestCase{
	
	@Test
	public void testDoubleTimeIndexDataBuffer() {
		BasicDoubleTimeIndexDataBuffer<Short, ShortUnit> buffer =
				new BasicDoubleTimeIndexDataBuffer<Short, ShortUnit>(new NPP(1, (byte)1, (byte)1), ShortUnit.class, 2, 1000, 60, 3);
		for(int i=0; i< 60; i++) {
			buffer.addElement(100000 + 3 * i, new ShortUnit(new Integer(i).shortValue()));
			buffer.addElement(100000 + 3 * i + 1, new ShortUnit(new Integer(i + 1).shortValue()));
			buffer.addElement(100000 + 3 * i + 2, new ShortUnit(new Integer(i + 2).shortValue()));
			buffer.addIndex();	
		}
		
		List<DataPoint<Short>> data = buffer.readDataPoint(3);
		assertEquals(data.size(), 9);
		System.out.println(data.toString());
	}
}
