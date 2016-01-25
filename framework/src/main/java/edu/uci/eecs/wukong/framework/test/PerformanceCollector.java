package edu.uci.eecs.wukong.framework.test;

import java.util.Map;
import java.util.HashMap;

public class PerformanceCollector {
	private Map<Short, Double> responseTimeMap;
	private Map<Short, Integer> messageCountMap;
	private Map<Short, Long> sendTimeMap;
	
	public PerformanceCollector() {
		responseTimeMap = new HashMap<Short, Double> ();
		messageCountMap = new HashMap<Short, Integer> ();
		sendTimeMap = new HashMap<Short, Long> ();
	}
	
	/**
	 * 
	 * @param port the port of target WuObject
	 */
	public void send(short port) {
		if (!sendTimeMap.containsKey(port)) {
			responseTimeMap.put(port, 0.0);
			messageCountMap.put(port, 0);
		}
		
		sendTimeMap.put(port, System.currentTimeMillis());
	}

	/**
	 * 
	 * @param port the port of target WuObject
	 */
	public void receive(short port) {
		if (sendTimeMap.containsKey(port) && responseTimeMap.containsKey(port) && messageCountMap.containsKey(port)) {
			long responsetime = System.currentTimeMillis() - sendTimeMap.get(port);
			messageCountMap.put(port, messageCountMap.get(port) + 1);
			responseTimeMap.put(port, responseTimeMap.get(port) + responsetime);
		}
	}
}
