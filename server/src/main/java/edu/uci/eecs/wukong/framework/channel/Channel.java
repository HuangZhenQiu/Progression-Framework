package edu.uci.eecs.wukong.framework.channel;

import java.util.HashSet;
import java.util.Set;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;

/**
 * The abstract class for pub/sub channel between progression server to PrClass.
 * It defines basic operations for pub/sub communication.
 * 
 * @author peter
 *
 * @param <T>
 */
public abstract class Channel<T> {
	
	protected Set<Channelable<T>> listeners;
	
	public Channel() {
		this.listeners = new HashSet<Channelable<T>>();
	}
	
	public synchronized void addListener(Channelable<T> listener) {
		// Channel will be used only for progression extenson for now.
		if (listener instanceof AbstractExecutionExtension) {
			this.listeners.add(listener);
		}
	}
	
	public synchronized void removeListener(Channelable<T> listener) {
		this.listeners.remove(listener);
	}
}
