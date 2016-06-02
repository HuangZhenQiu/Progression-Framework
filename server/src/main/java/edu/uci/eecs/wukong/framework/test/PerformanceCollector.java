package edu.uci.eecs.wukong.framework.test;

import java.util.Map;
import java.util.HashMap;

public class PerformanceCollector {	
	private Map<Byte, Long> messageCountMap;
	// Map port id of target wuobject to <message id, send out time stamp>
	private Map<Byte, Map<Long, Long>> sendTimeMap;
	// Map port id of target wuobject to <message id, response time>
	private Map<Byte, Map<Long, Long>> responseTimeMap;
	
	public PerformanceCollector() {
		responseTimeMap = new HashMap<Byte, Map<Long, Long>> ();
		messageCountMap = new HashMap<Byte, Long> ();
		sendTimeMap = new HashMap<Byte, Map<Long, Long>> ();
	}
	
	/**
	 * 
	 * @param port the port of target WuObject
	 * @param messageId the id of the message
	 */
	public void send(Byte port, Long messageId) {
		if (!messageCountMap.containsKey(port)) {
			messageCountMap.put(port, 0L);
			sendTimeMap.put(port, new HashMap<Long, Long> ());
			responseTimeMap.put(port, new HashMap<Long, Long> ());
		}
		
		sendTimeMap.get(port).put(messageId, System.currentTimeMillis());
	}

	/**
	 * 
	 * @param port the port of target WuObject
	 */
	public void receive(Byte port, Long messageId) {
		if (sendTimeMap.containsKey(port) && responseTimeMap.containsKey(port) && messageCountMap.containsKey(port)) {
			long responsetime = System.currentTimeMillis() - sendTimeMap.get(port).get(sendTimeMap);
			messageCountMap.put(port, messageCountMap.get(port) + 1);
			responseTimeMap.get(port).put(messageId, responsetime);
		}
	}
}
