package edu.uci.eecs.wukong.framework.channel;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import edu.uci.eecs.wukong.framework.model.NPP;

/**
 * Channel is used to store real-time data for user's action signal from device.
 * Progression Extensions can define how to use these data 
 * 
 * 
 * @author Peter
 *
 */
public class Channel<T extends Number> {
	private NPP key;
	private Queue<T> queue; // TODO leave it here for user operation optimization
	private List<ChannelListener<T>> listeners;
	
	public Channel(NPP key) {
		this.key = key;
		this.queue = new ArrayDeque<T>();
		this.listeners = new ArrayList<ChannelListener<T>>();
	}

	public NPP getKey() {
		return key;
	}

	public void setKey(NPP key) {
		this.key = key;
	}
	
	public Type getType() {
		Type type = this.getClass().getGenericSuperclass();
		return ((ParameterizedType)type).getActualTypeArguments()[0];
	}
	
	public synchronized void append(T data) {
		for (ChannelListener<T> listener : listeners) {
			listener.onMessage(data);
		}
	}
	
	public synchronized void addListener(ChannelListener listener) {
		this.listeners.add(listener);
	}
}
