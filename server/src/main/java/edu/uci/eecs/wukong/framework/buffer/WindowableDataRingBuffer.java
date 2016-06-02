package edu.uci.eecs.wukong.framework.buffer;

import edu.uci.eecs.wukong.framework.api.Windowable;

import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WindowableDataRingBuffer<T, E extends BufferUnit<T>> extends DataRingBuffer<T, E> {

	private Set<Windowable<T>> windowables;
	
	public WindowableDataRingBuffer(int capacity, int unitSize, Class<E> type) {
		super(capacity, unitSize, type);
		this.windowables = new HashSet<Windowable<T>> ();
	}
	
	public void addWindowable(Windowable<T> windowable) {
		windowables.add(windowable);
	}
	
	@Override
	public synchronized void addElement(long time, E value) {
		super.addElement(time, value);
		List<T> data = this.getElements(this.getUnitSize());
		if (super.isFull()) {
			Iterator<Windowable<T>> iter =  windowables.iterator();
			while (iter.hasNext()) {
				iter.next().window(data);
			}
			super.clear();
 		}
	}
}
