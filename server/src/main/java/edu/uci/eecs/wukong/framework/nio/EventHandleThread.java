package edu.uci.eecs.wukong.framework.nio;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.model.MPTNPackage;
import edu.uci.eecs.wukong.framework.mptn.MPTNMessageListener;

public class EventHandleThread implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(EventHandleThread.class);
    private ByteBuffer buffer;
    private SocketAddress remoteAddress;
    private List<MPTNMessageListener> listeners;
    
	public EventHandleThread(SocketAddress remoteAddress, ByteBuffer buffer, List<MPTNMessageListener> listeners) {
		this.buffer = buffer;
		this.remoteAddress = remoteAddress;
		this.listeners = listeners;
	}
	
	public void run() { 
		try {
			fireMPTNMessage(buffer);
			buffer.clear();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Fail to handle event message: " + e.toString());
		}
	}
	
	public void fireMPTNMessage(ByteBuffer bytes) {
		for (MPTNMessageListener listener : listeners) {
			try {
				// For realtime processing, it will call to Prclas logic, which is unsafe
				MPTNPackage mptn = new MPTNPackage(bytes);
				listener.onMessage(remoteAddress, mptn);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failt to handle event for listener " + listener.getClass());
			}
		}
	}
}
