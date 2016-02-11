package edu.uci.eecs.wukong.framework.operator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.uci.eecs.wukong.framework.buffer.DataPoint;
import edu.uci.eecs.wukong.framework.model.NPP;
import junit.framework.TestCase;

public class OperatorTest extends TestCase {
	private NPP npp = new NPP(1l, (byte)1, (byte)1);
	private DataPoint<Float> dataA = new DataPoint<Float>(npp, 1, 1.0f);
	private DataPoint<Float> dataB = new DataPoint<Float>(npp, 1, 2.0f);
	private DataPoint<Float> dataC = new DataPoint<Float>(npp, 1, 3.0f);
	private DataPoint<Float> dataD = new DataPoint<Float>(npp, 1, 4.0f);
	
	@Test
	public void testMinOperator() {
		MinOperator<Float> floatMin = new MinOperator<Float> (Float.class);
		List<DataPoint<Float>> floatPoints = new ArrayList<DataPoint<Float>> ();
		floatPoints.add(dataA);
		floatPoints.add(dataB);
		floatPoints.add(dataB);
		floatPoints.add(dataD);
		
		try {
			Float min = floatMin.operate(floatPoints);
			assertEquals(min, 1.0f);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testMaxOperator() {
		MaxOperator<Float> floatMax = new MaxOperator<Float> (Float.class);
		List<DataPoint<Float>> floatPoints = new ArrayList<DataPoint<Float>> ();
		floatPoints.add(dataA);
		floatPoints.add(dataB);
		floatPoints.add(dataB);
		floatPoints.add(dataD);
		
		try {
			Float max = floatMax.operate(floatPoints);
			assertEquals(max, 4.0f);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testAverageOperator() {
		AverageOperator<Float> floatAvr = new AverageOperator<Float> (Float.class);
		List<DataPoint<Float>> floatPoints = new ArrayList<DataPoint<Float>> ();
		floatPoints.add(dataA);
		floatPoints.add(dataB);
		floatPoints.add(dataC);
		floatPoints.add(dataD);
		
		try {
			Float avr = floatAvr.operate(floatPoints);
			assertEquals(avr, 2.5f);
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testMimoOperator() {
		
	}
}
