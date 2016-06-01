package edu.uci.eecs.wukong.framework.operator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.operator.basic.MaxOperator;
import edu.uci.eecs.wukong.framework.operator.basic.MinOperator;
import junit.framework.TestCase;

public class OperatorTest extends TestCase {
	private NPP npp = new NPP(1l, (byte)1, (byte)1);
	private DataPoint<Short> dataA = new DataPoint<Short>(npp, 1, (short)1);
	private DataPoint<Short> dataB = new DataPoint<Short>(npp, 1, (short)2);
	private DataPoint<Short> dataC = new DataPoint<Short>(npp, 1, (short)3);
	private DataPoint<Short> dataD = new DataPoint<Short>(npp, 1, (short)4);
	
	@Test
	public void testMinOperator() {
		MinOperator floatMin = new MinOperator ();
		List<DataPoint<Short>> floatPoints = new ArrayList<DataPoint<Short>> ();
		floatPoints.add(dataA);
		floatPoints.add(dataB);
		floatPoints.add(dataB);
		floatPoints.add(dataD);
		
		try {
			Double min = floatMin.operate(floatPoints);
			assertEquals(min, 1.0d);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testMaxOperator() {
		MaxOperator floatMax = new MaxOperator ();
		List<DataPoint<Short>> floatPoints = new ArrayList<DataPoint<Short>> ();
		floatPoints.add(dataA);
		floatPoints.add(dataB);
		floatPoints.add(dataB);
		floatPoints.add(dataD);
		
		try {
			Double max = floatMax.operate(floatPoints);
			assertEquals(max, 4.0d);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testAverageOperator() {
		AverageOperator floatAvr = new AverageOperator ();
		List<DataPoint<Short>> floatPoints = new ArrayList<DataPoint<Short>> ();
		floatPoints.add(dataA);
		floatPoints.add(dataB);
		floatPoints.add(dataC);
		floatPoints.add(dataD);
		
		try {
			Double avr = floatAvr.operate(floatPoints);
			assertEquals(avr, 2.5d);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testMimoOperator() {
		
	}
}
