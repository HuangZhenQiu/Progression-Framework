package edu.uci.eecs.wukong.framework.channel;

import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Map.Entry;  
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.model.NPP;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;

/**
 * Channel is used to store real-time data for user's action signal from device.
 * Progression Extensions can define how to use these data 
 * 
 * 
 * @author Peter
 *
 */
public class Channel<T> {
	private final static Logger LOGGER = LoggerFactory.getLogger(PipelinePrClass.class);
	private NPP key;
	private Queue<T> queue; // TODO leave it here for user operation optimization
	private Set<Channelable> listeners;
	private Map<Field, Set<WuObjectModel>> fieldMap;
	
	public Channel(NPP key) {
		this.key = key;
		this.queue = new ArrayDeque<T>();
		this.listeners = new HashSet<Channelable>();
		this.fieldMap = new HashMap<Field, Set<WuObjectModel>>();
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
		
		for (Entry<Field, Set<WuObjectModel>> entry : fieldMap.entrySet()) {
			for (WuObjectModel model : entry.getValue()) {
				try {
					entry.getKey().set(model.getPrClass(), data);
				} catch (IllegalAccessException e) {
					LOGGER.error("Fail to set field value for " + entry.getKey().getName() + " of PrClass in channel " + key.toString());
				}
			}
		}
	}
	
	public synchronized void addListener(Channelable listener) {
		// Channel will be used only for progression extenson for now.
		if (listener instanceof AbstractProgressionExtension) {
			this.listeners.add(listener);
		}
	}
	
	public synchronized void removeListener(Channelable listener) {
		this.listeners.remove(listener);
	}
	
	public synchronized void addField(String fieldName, WuObjectModel model) {
		try {
			Class cls = model.getPrClass().getClass();
			Field field = cls.getDeclaredField(fieldName);
			field.setAccessible(true);
			if (!fieldMap.containsKey(field)) {
				Set<WuObjectModel> set = new HashSet<WuObjectModel>();
				fieldMap.put(field, set);
			}
			fieldMap.get(field).add(model);
		} catch (Exception e) {
			LOGGER.error("Fail to add field " + fieldName + " of " + model);
		}
	}
	
	public synchronized void removeField(String fieldName, WuObjectModel model) {
		try {
			Class cls = model.getPrClass().getClass();
			Field field = cls.getDeclaredField(fieldName);
			fieldMap.get(field).remove(model);
		} catch (Exception e) {
			LOGGER.error("Fail to remove field " + fieldName + " of " + model);
		}
	}
}
