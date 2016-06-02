package edu.uci.eecs.wukong.framework.util;

import java.util.concurrent.ThreadFactory;

public class DaemanThreadFactory implements ThreadFactory {
	public final static String PROGRESSION_THREAD_NAME_PREFIX = "PROGRESSION-";
	private String name;
	
	public DaemanThreadFactory(String name) {
		this.name = name;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(true);
		if (name != null) {
			thread.setName(PROGRESSION_THREAD_NAME_PREFIX + name);
		}
		
		return thread;
	}
}
