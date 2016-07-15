package edu.uci.eecs.wukong.prclass.occupancy;

import java.lang.Comparable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.time.LocalDateTime;

import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.buffer.RingBuffer;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.predict.Predict;
import edu.uci.eecs.wukong.prclass.occupancy.OccupancyDetection;

import com.google.common.annotations.VisibleForTesting;

public class ODProgressionExtension extends AbstractExecutionExtension<OccupancyDetection>
	implements Executable<Byte>, TimerExecutable {
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
	public void execute(List<Byte> data, ExecutionContext context) {
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
			// queue need to build every time
			queue.clear();
			LocalDateTime time = LocalDateTime.now();
			int slotsUtilNow = time.getHour() * 4 + time.getMinute() / 15;
			byte[] currentSlots = new byte[slotsUtilNow];
			buffer.getByteFromPosition(currentSlots, slotsUtilNow);
			for (int i = 1; i < days; i++) {
				byte[] slots = new byte[24 * 4]; 
				// get occupancy slots i days before
				buffer.getByteFromPosition(slots, slotsUtilNow + i * slots.length);
				int distance = calculateHammingDistance(currentSlots, slots);
				DaySlots daySlots = new DaySlots(slots, distance);
				queue.add(daySlots);
			}
			boolean occupancy = predict(slotsUtilNow + 1);
			if (occupancy == false) {
				// trigger re-mapping for user absent mode
				List<Predict> predicts = new ArrayList<Predict> ();
				this.getPrClass().remap(predicts);
			} else {
				// trigger re-mapping for user occupancy mode for 
				// trigger re-mapping for user absent mode
				List<Predict> predicts = new ArrayList<Predict> ();
				this.getPrClass().remap(predicts);
			}
		}
	}
	
	@VisibleForTesting
	public void loadData(List<List<Byte>> observations) {
		buffer.clear();
		this.days = observations.size() + 1;
		for (int i = 0; i < observations.size(); i++) {
			for (int j = 0; j < observations.get(i).size(); j ++) {
				buffer.appendByte(observations.get(i).get(j));
			}
		}
	}
	
	@VisibleForTesting
	public boolean predict(List<Byte> observation, int size) {
		queue.clear();
		for (int i = 0; i < this.days; i++) {
			byte[] slots = new byte[size];
			buffer.getByteFromPosition(slots, slots.length);
			int distance = calculateHammingDistance(observation, slots);
			DaySlots daySlots = new DaySlots(slots, distance);
			queue.add(daySlots);
		}
		
		return predict(observation.size() + 1);
	}
	
	private boolean predict(int slot) {
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
			return true;
		}
		return false;
	}
	
	private int calculateHammingDistance(List<Byte> source, byte[] dest) {
		byte[] bsource = new byte[source.size()];
		int i = 0;
		for (Byte b : source) {
			bsource[i++] = b;
		}
		
		return calculateHammingDistance(bsource, dest);
	}
	
	private int calculateHammingDistance(byte[] source, byte[] dest) {
		int distance = 0;
		int min = source.length > dest.length ? dest.length : source.length;
		for (int i = 0; i < min; i++) {
			if (source[i] != dest[i]) {
				distance ++;
			}
		}
		
		return distance;
	}
}
