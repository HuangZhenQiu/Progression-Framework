package edu.uci.eecs.wukong.framework.channel;

import java.util.List;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;

/**
 * Channel is used to store real-time data for user's action signal from device.
 * Progression Extensions can define how to use these data 
 * 
 * 
 * @author Peter
 *
 */
public class Channel {
	private NPP key;
	private Queue<Short> queue; // TODO leave it here for user operation optimization
	private List<Channelable> listeners;
	
	public Channel(NPP key) {
		this.key = key;
		this.queue = new ArrayDeque<Short>();
		this.listeners = new ArrayList<Channelable>();
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
	
	public synchronized void append(short data) {
		ChannelData channelData = new ChannelData(key, data);
		for (Channelable listener : listeners) {
			listener.execute(channelData);
		}
	}
	
	public synchronized void addListener(Channelable listener) {
		// Channel will be used only for progression extenson for now.
		if (listener instanceof AbstractProgressionExtension) {
			this.listeners.add(listener);
		}
	}
}
