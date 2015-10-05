package edu.uci.eecs.wukong.framework.manager;

import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.client.XMPPContextClient;
import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.DemoContext;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.context.ContextListener;
import edu.uci.eecs.wukong.framework.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ContextManager {
	private static Logger logger = LoggerFactory.getLogger(ContextManager.class);
	private static Gson gson = new Gson();
	private Map<Plugin, List<String>> pluginContextMap;
	private Map<String, Context> contexts;
	private XMPPContextClient contextClient;
	private Set<String> topicFilterSet;
	private ContextEventListenser contextListener;
	private List<ContextListener> listeners;
	
	public ContextManager() {
		pluginContextMap = new HashMap<Plugin, List<String>>();
		contexts = new ConcurrentHashMap<String, Context>();
		contextClient = XMPPContextClient.getInstance();
		topicFilterSet = new HashSet<String>();
		contextListener = new ContextEventListenser();
		listeners = new ArrayList<ContextListener>();
	}
	
	private class ContextEventListenser implements ItemEventListener<PayloadItem<Context>> {
		public void handlePublishedItems(ItemPublishEvent evt){
			for (Object object :evt.getItems()) {
				PayloadItem item = (PayloadItem)object;
				String message = item.getPayload().toString();
				logger.info(message);
				int start = message.indexOf('{');
				int end = message.indexOf('}');
				DemoContext context = gson.fromJson(message.substring(start, end + 1), DemoContext.class);
				contexts.put(context.getTopicId(), context);
				for(ContextListener listener : listeners) {
					listener.onContextArrival(context);
				}
			}
		}
	}
	
	public synchronized void subsribeContext(ContextListener listener) {
		this.listeners.add(listener);
	}
	
	public synchronized void unsubsribeContext(ContextListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}
	
	public void subscribe(Plugin plugin, List<String> topics) {
		for (String topic : topics) {
			if(!topicFilterSet.contains(topic)) {
				contextClient.subscribe(topic, contextListener);
				topicFilterSet.add(topic);
			}
		}
		
		pluginContextMap.put(plugin, topics);
		logger.info("Finish register plugn " + plugin.getName() + " in context manager.");
	}
	
	public synchronized ExecutionContext getPluginExecutionContext(Plugin plugin) {
		List<String> subscribedTopics = pluginContextMap.get(plugin);
		Map<String, Context> pluginContext = new HashMap<String, Context>();
		for(String topic : subscribedTopics) {
			pluginContext.put(topic, contexts.get(topic));
		}
		return new ExecutionContext(pluginContext);
	}
}
