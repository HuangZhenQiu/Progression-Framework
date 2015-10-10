package edu.uci.eecs.wukong.framework.nio;

import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.wkpf.MPTNMessageListener;

public class EventHandleThread implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(EventHandleThread.class);
    private ByteBuffer buffer;
    private List<MPTNMessageListener> listeners;
    
	public EventHandleThread(ByteBuffer buffer, List<MPTNMessageListener> listeners) {
		this.buffer = buffer;
		this.listeners = listeners;
	}
	
	public void run() { 
		try {
			fireMPTNMessage(buffer);
			buffer.clear();
		} catch (Exception e) {
			logger.error("Fail to handle event message: " + e.toString());
		}
	}
	
	public void fireMPTNMessage(ByteBuffer bytes) {
		for (MPTNMessageListener listener : listeners) {
			listener.onMessage(bytes);
		}
	}
}
