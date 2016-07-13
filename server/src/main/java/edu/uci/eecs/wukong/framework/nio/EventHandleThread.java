package edu.uci.eecs.wukong.framework.nio;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.mptn.MPTNMessageListener;
import edu.uci.eecs.wukong.framework.mptn.MPTNPackageParser;
import edu.uci.eecs.wukong.framework.mptn.packet.AbstractMPTNPacket;

public class EventHandleThread<T extends AbstractMPTNPacket> implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(EventHandleThread.class);
    private ByteBuffer buffer;
    private SocketAddress remoteAddress;
    private MPTNPackageParser<T> parser;
    private List<MPTNMessageListener<T>> listeners;
    
	public EventHandleThread(Class<T> type, SocketAddress remoteAddress, ByteBuffer buffer,
			List<MPTNMessageListener<T>> listeners) {
		this.buffer = buffer;
		this.remoteAddress = remoteAddress;
		this.listeners = listeners;
		this.parser = new MPTNPackageParser<T>(type);
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
		for (MPTNMessageListener<T> listener : listeners) {
			try {
				// For real-time processing, it will call to Prclass logic, which is unsafe
				T message = parser.parse(bytes);
				if (message != null) {
					listener.onMessage(remoteAddress, message);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failt to handle event for listener " + listener.getClass());
			}
		}
	}
}
