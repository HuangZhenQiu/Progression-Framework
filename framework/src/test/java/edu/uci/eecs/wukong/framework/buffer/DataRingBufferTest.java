package edu.uci.eecs.wukong.framework.buffer;

import java.util.List;

import org.junit.Test;

import edu.uci.eecs.wukong.framework.buffer.BufferUnits.ByteUnit;
import edu.uci.eecs.wukong.framework.buffer.BufferUnits.IntUnit;
import edu.uci.eecs.wukong.framework.buffer.BufferUnits.ShortUnit;
import edu.uci.eecs.wukong.framework.property.Location;
import edu.uci.eecs.wukong.framework.property.Activity;

import junit.framework.TestCase;

public class DataRingBufferTest extends TestCase {
	private Byte byteA = 1;
	private Byte byteB = 2;
	private Byte byteC = 3;
	private Short shortA = 1;
	private Short shortB = 2;
	private Short shortC = 3;
	private Integer intA = 3;
	private Integer intB = 4;
	private Integer intC = 5;
	private Location locationA = new Location(4.0f, 5.0f, 6.0f);
	private Location locationB = new Location(1.0f, 2.0f, 3.0f);
	private Location locationC = new Location(0.1f, 0.2f, 0.3f);
	private Activity activityA = new Activity(0l, (short)0, 6.0f);
	private Activity activityB = new Activity(1l, (short)1, 7.0f);
	private Activity activityC = new Activity(2l, (short)2, 8.0f);
	
	@Test
	public void testByteDataRingBuffer() {
		DataRingBuffer<Byte, ByteUnit> buffer = new DataRingBuffer<Byte, ByteUnit> (100, 1, ByteUnit.class);
		buffer.addElement(System.currentTimeMillis(), new ByteUnit(byteA));
		buffer.addElement(System.currentTimeMillis(), new ByteUnit(byteB));
		buffer.addElement(System.currentTimeMillis(), new ByteUnit(byteC));
		List<Byte> bytes = buffer.getElements(3);
		assertEquals(byteA, bytes.get(0));
		assertEquals(byteB, bytes.get(1));
		assertEquals(byteC, bytes.get(2));
	}
	
	@Test
	public void testByteDataRingBufferBoundary() {
		DataRingBuffer<Byte, ByteUnit> buffer = new DataRingBuffer<Byte, ByteUnit> (1, 1, ByteUnit.class);
		buffer.addElement(System.currentTimeMillis(), new ByteUnit(byteA));
		buffer.addElement(System.currentTimeMillis(), new ByteUnit(byteB));
		List<Byte> bytes = buffer.getElements(1);
		assertEquals(byteB, bytes.get(0));
	}
	
	@Test
	public void testShortDataRingBuffer() {
		DataRingBuffer<Short, ShortUnit> buffer = new DataRingBuffer<Short, ShortUnit> (100, 2, ShortUnit.class);
		buffer.addElement(System.currentTimeMillis(),  new ShortUnit(shortA));
		buffer.addElement(System.currentTimeMillis(),  new ShortUnit(shortB));
		buffer.addElement(System.currentTimeMillis(), new ShortUnit(shortC));
		 List<Short> shorts = buffer.getElements(3);
		 assertEquals(shortA, shorts.get(0));
		 assertEquals(shortB, shorts.get(1));
		 assertEquals(shortC, shorts.get(2));
	}
	
	public void testShortDataRingBufferBoundary() {
		DataRingBuffer<Short, ShortUnit> buffer = new DataRingBuffer<Short, ShortUnit> (2, 2, ShortUnit.class);
		buffer.addElement(System.currentTimeMillis(),  new ShortUnit(shortA));
		buffer.addElement(System.currentTimeMillis(),  new ShortUnit(shortB));
		buffer.addElement(System.currentTimeMillis(), new ShortUnit(shortC));
		 List<Short> shorts = buffer.getElements(2);
		 assertEquals(shortB, shorts.get(0));
		 assertEquals(shortC, shorts.get(1));
	}
	
	@Test
	public void testIntegerDataRingBuffer() {
		DataRingBuffer<Integer, IntUnit> buffer = new DataRingBuffer<Integer, IntUnit> (100, 4, IntUnit.class);
		buffer.addElement(System.currentTimeMillis(),  new IntUnit(intA));
		buffer.addElement(System.currentTimeMillis(),  new IntUnit(intB));
		buffer.addElement(System.currentTimeMillis(), new IntUnit(intC));
		 List<Integer> ints = buffer.getElements(3);
		 assertEquals(intA, ints.get(0));
		 assertEquals(intB, ints.get(1));
		 assertEquals(intC, ints.get(2));
	}
	
	@Test
	public void testLocationDataRingBuffer() {
		DataRingBuffer<Location, LocationUnit> buffer =
				new DataRingBuffer<Location, LocationUnit> (100, 12, LocationUnit.class);
		buffer.addElement(System.currentTimeMillis(), new LocationUnit(locationA));
		buffer.addElement(System.currentTimeMillis(), new LocationUnit(locationB));
		buffer.addElement(System.currentTimeMillis(), new LocationUnit(locationC));
		List<Location> locations = buffer.getElements(3);
		assertEquals(locationA, locations.get(0));
		assertEquals(locationB, locations.get(1));
		assertEquals(locationC, locations.get(2));
	}
	
	@Test
	public void testActivityDataRingBuffer() {
		DataRingBuffer<Activity, ActivityUnit> buffer =
				new DataRingBuffer<Activity, ActivityUnit> (100, 14, ActivityUnit.class);
		buffer.addElement(System.currentTimeMillis(), new ActivityUnit(activityA));
		buffer.addElement(System.currentTimeMillis(), new ActivityUnit(activityB));
		buffer.addElement(System.currentTimeMillis(), new ActivityUnit(activityC));
		List<Activity> activities = buffer.getElements(3);
		assertEquals(activityA, activities.get(0));
		assertEquals(activityB, activities.get(1));
		assertEquals(activityC, activities.get(2));
	}
}
