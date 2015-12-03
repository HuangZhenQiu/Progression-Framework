package edu.uci.eecs.wukong.prclass.occupancy;

import java.lang.Comparable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Iterator;
import java.time.LocalDateTime;

import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.buffer.RingBuffer;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.prclass.occupancy.OccupancyDetection;

public class ODProgressionExtension extends AbstractProgressionExtension
	implements Executable, TimerExecutable {
	private OccupancyDetection oc;
	private RingBuffer buffer;
	private PriorityQueue<DaySlots> queue;
	private int days;
	
	public ODProgressionExtension(OccupancyDetection oc) {
		super(oc);
		this.oc = oc;
		// Make sure its init values are initialized by reprogramming
		this.days = oc.getDays();
		int unit = 24 * 60 / oc.getInterval() * oc.getDays();
		buffer = new RingBuffer(unit);
		queue =  new PriorityQueue<DaySlots>(oc.getTopK());
	}
	
	public static class DaySlots implements Comparable<DaySlots>{
		private int distance;
		private byte[] data;
		
		public DaySlots(byte[] data, int distance) {
			this.distance = distance;
			this.data = data;
		}
		
		public int compareTo(DaySlots slots) {
			return this.distance - slots.distance;
		}
	}

	/**
	 * Observe the occupancy signal from feature extraction
	 * 
	 */
	@Override
	public void execute(List data, ExecutionContext context) {
		if (data != null && !data.isEmpty()) {
			byte occupancy = (Byte) data.get(0);
			buffer.appendByte(occupancy);
		}
	}
	
	
	/**
	 * Periodically predict the occupancy of next 15 minutes, and publish it as a factor.
	 * The value will be cached in factor cache, and accessible to another Prclass that
	 * subscribes to "occupancy" topic
	 */
	@Override
	@WuTimer(interval = 60 * 15)
	public void execute() {
		if (buffer.isFull()) {
			LocalDateTime time = LocalDateTime.now();
			int slotsUtilNow = time.getHour() * 4 + time.getMinute() / 15;
			byte[] currentSlots = new byte[slotsUtilNow];
			buffer.getByteFromPosition(currentSlots, slotsUtilNow);
			for (int i = 1; i < days; i++) {
				byte[] slots = new byte[24 * 4]; 
				// get occupancy slots i days before
				buffer.getByteFromPosition(slots, slotsUtilNow + i * slots.length);
				int distance = calculateHammingDistance(currentSlots, slots, slotsUtilNow);
				DaySlots daySlots = new DaySlots(slots, distance);
				queue.add(daySlots);
			}
			predict(slotsUtilNow + 1);
		}
	}
	
	private void predict(int slot) {
		int occupiedDays = 0;
		Iterator<DaySlots> iterator = queue.iterator();
		while(iterator.hasNext()) {
			DaySlots slots = iterator.next();
			if (slots.data[slot] == 1) {
				occupiedDays ++ ;
			}
		}
		
		double prob = occupiedDays / (double)days;
		if (prob > 0.5) {
			oc.setOccupancy(true);
		}
	}
	
	private int calculateHammingDistance(byte[] source, byte[] dest, int length) {
		int distance = 0;
		for (int i = 0; i < length; i++) {
			if (source[i] != dest[i]) {
				distance ++;
			}
		}
		
		return distance;
	}
}
