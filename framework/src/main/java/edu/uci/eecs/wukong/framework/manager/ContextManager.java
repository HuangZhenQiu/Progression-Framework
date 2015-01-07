package edu.uci.eecs.wukong.framework.manager;

import edu.uci.eecs.wukong.framework.Plugin;
import edu.uci.eecs.wukong.framework.client.XMPPContextClient;
import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;

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
	private Map<Plugin, List<String>> pluginContextMap;
	private Map<String, Context> contexts;
	private XMPPContextClient contextClient;
	private Set<String> topics;
	private ContextEventListenser contextListener;
	
	public ContextManager() {
		pluginContextMap = new HashMap<Plugin, List<String>>();
		contexts = new ConcurrentHashMap<String, Context>();
		contextClient = XMPPContextClient.getInstance();
		topics = new HashSet<String>();
		contextListener = new ContextEventListenser();
	}
	
	private class ContextEventListenser implements ItemEventListener<PayloadItem<Context>> {
		public void handlePublishedItems(ItemPublishEvent evt){
			for (Object object :evt.getItems()) {
				PayloadItem<Context> item = (PayloadItem<Context>)object;
				Context context = item.getPayload();
				contexts.put(context.getTopicId(), context);;
			}
		}
	}
	
	public void subscribe(Plugin plugin, List<String> topics) {
		for (String topic : topics) {
			if(!topics.contains(topic)) {
				contextClient.subscribe(topic, contextListener);
				topics.add(topic);
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
