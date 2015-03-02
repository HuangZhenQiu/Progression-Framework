package edu.uci.eecs.wukong.framework.buffer;

import junit.framework.TestCase;
import static org.junit.Assert.*;

import java.util.List;
import edu.uci.eecs.wukong.framework.buffer.DoubleTimeIndexDataBuffer.DataPoint;

public class DoubleTimeIndexDataBufferTest extends TestCase{
	
	public void testDoubleTimeIndexDataBuffer() {
		DoubleTimeIndexDataBuffer buffer = new DoubleTimeIndexDataBuffer(1000, 60, 3);
		for(int i=0; i< 60; i++) {
			buffer.addElement(100000 + 3 * i, new Integer(i).shortValue());
			buffer.addElement(100000 + 3 * i + 1, new Integer(i).shortValue());
			buffer.addElement(100000 + 3 * i + 2, new Integer(i).shortValue());
			buffer.addIndex();	
		}
		
		List<DataPoint> data = buffer.readDataPoint(3);
		assertEquals(data.size(), 9);
		System.out.println(data.toString());
	}

}
