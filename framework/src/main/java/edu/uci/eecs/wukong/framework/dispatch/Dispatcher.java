/*******************************************************************************
 * Copyright (c) 2013  UCI EECS.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://link.eecs.uci.edu/LLAMA/esb.php
 *
 * Contributors:
 *     LLAMA Project Group Member
 *******************************************************************************/
package edu.uci.eecs.wukong.framework.dispatch;

import edu.uci.eecs.wukong.framework.event.Event;
import edu.uci.eecs.wukong.framework.event.ReasonEvent;
import edu.uci.eecs.wukong.framework.event.RuleMatchEvent;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class Dispatcher {
	
	private static Dispatcher dispatcher;
	
	private BlockingQueue<Runnable> inputEventQueue;
	private BlockingQueue<Runnable> processQueue;
	
	
	private ThreadPoolExecutor inputEventExecutor;
	private ThreadPoolExecutor taskExecutor;
	
	public static Dispatcher instance() {
		if(dispatcher == null) {
			dispatcher = new Dispatcher();
		}
		
		return dispatcher;
	}
	
	private Dispatcher() {
		
		inputEventQueue = new PriorityBlockingQueue<Runnable>();
		processQueue = new PriorityBlockingQueue<Runnable>();
		inputEventExecutor = new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, inputEventQueue);
		taskExecutor =  new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, processQueue);
	}
	
	public void start() {
		
		inputEventExecutor.prestartCoreThread();
		taskExecutor.prestartCoreThread();	
	}
	
	public void shutdown() {
		
		//TODO huangzhenqiuo825: handle the pending task within queues. 
		
		inputEventExecutor.shutdown();
		taskExecutor.shutdown();
	}
	
	public boolean addEventTask(EventDispatchTask<Event> task){
		
		return inputEventQueue.add(task);
	}
	
	public boolean addMatchTask(JessRuleMatchTask<RuleMatchEvent> task) {
		
		return processQueue.add(task);
	}
	
	public boolean addReasonTask(ReasonEngineTask<ReasonEvent> task) {
		
		return processQueue.add(task);
	}


}
