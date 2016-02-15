package edu.uci.eecs.wukong.framework.buffer;

import static org.junit.Assert.assertEquals;
import java.util.List;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import edu.uci.eecs.wukong.framework.api.Windowable;
import edu.uci.eecs.wukong.framework.property.Location;

import junit.framework.JUnit4TestAdapter;

public class WindowableDataRingBufferTest{
	private Location locationA = new Location(4.0f, 5.0f, 6.0f);
	private Location locationB = new Location(1.0f, 2.0f, 3.0f);
	private Location locationC = new Location(0.1f, 0.2f, 0.3f);
	private WindowableDataRingBuffer<Location, LocationUnit> buffer;
	private WindowableTracker tracker;

	private class WindowableTracker implements Windowable<Location> {
		private int count = 0;
		
		@Override
		public void window(List<Location> data) {
			if (data.size() == 3
					&& data.get(0).equals(locationA)
					&& data.get(1).equals(locationB)
					&& data.get(2).equals(locationC)) {
				count ++;
			}
		}
		
		public int getCount() {
			return this.count;
		}
	}
	
	@Before
	public void setup() {
		buffer = new WindowableDataRingBuffer<Location, LocationUnit> (3, 12, LocationUnit.class);
		tracker = new WindowableTracker();
		buffer.addWindowable(tracker);
		buffer.addElement(System.currentTimeMillis(), new LocationUnit(locationA));
		buffer.addElement(System.currentTimeMillis(), new LocationUnit(locationB));
		buffer.addElement(System.currentTimeMillis(), new LocationUnit(locationC));
	}
	
	@Test
	public void testAddElement() {
		List<Location> locations = new ArrayList<Location> ();
		locations.add(locationA);
		locations.add(locationB);
		locations.add(locationC);
		assertEquals(locations, buffer.getElements(3));
	}
	
	@Test
	public void testWindowableCallable() {
		assertEquals(tracker.getCount(), 1);
		assertEquals(buffer.isEmpty(), true);
		buffer.addElement(System.currentTimeMillis(), new LocationUnit(locationA));
		buffer.addElement(System.currentTimeMillis(), new LocationUnit(locationB));
		buffer.addElement(System.currentTimeMillis(), new LocationUnit(locationC));
		assertEquals(tracker.getCount(), 2);
	}
	
	public static junit.framework.Test suit() {
		return new JUnit4TestAdapter(WindowableDataRingBufferTest.class);
	}
}
