package edu.uci.eecs.wukong.framework.nio;

import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.nio.ChannelAttachment;
import edu.uci.eecs.wukong.framework.wkpf.MPTNMessageListener;

public class EventHandleThread implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(EventHandleThread.class);
    private SelectionKey key;
    private List<MPTNMessageListener> listeners;
    
	public EventHandleThread(SelectionKey key, List<MPTNMessageListener> listeners) {
		this.key = key;
		this.listeners = listeners;
	}
	
	public void run() { 
		try {
			DatagramChannel channel = (DatagramChannel) key.channel();
			ChannelAttachment attachment = (ChannelAttachment)key.attachment();
			attachment.setAddress(channel.receive(attachment.getBuffer()));
			attachment.getBuffer().flip();
			fireMPTNMessage(attachment.getBuffer().duplicate());
			attachment.getBuffer().clear();
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
