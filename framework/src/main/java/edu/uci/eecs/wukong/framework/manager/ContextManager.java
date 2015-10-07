package edu.uci.eecs.wukong.framework.manager;

import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.client.XMPPContextClient;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.prclass.demo.DemoFactor;

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
	private Map<PrClass, List<String>> pluginContextMap;
	private Map<String, BaseFactor> contexts;
	private XMPPContextClient contextClient;
	private Set<String> topicFilterSet;
	private ContextEventListenser contextListener;
	private List<FactorListener> listeners;
	
	public ContextManager() {
		pluginContextMap = new HashMap<PrClass, List<String>>();
		contexts = new ConcurrentHashMap<String, BaseFactor>();
		// contextClient = XMPPContextClient.getInstance();
		topicFilterSet = new HashSet<String>();
		contextListener = new ContextEventListenser();
		listeners = new ArrayList<FactorListener>();
	}
	
	private class ContextEventListenser implements ItemEventListener<PayloadItem<BaseFactor>> {
		public void handlePublishedItems(ItemPublishEvent evt){
			for (Object object :evt.getItems()) {
				PayloadItem item = (PayloadItem)object;
				String message = item.getPayload().toString();
				logger.info(message);
				int start = message.indexOf('{');
				int end = message.indexOf('}');
				DemoFactor context = gson.fromJson(message.substring(start, end + 1), DemoFactor.class);
				contexts.put(context.getTopicId(), context);
				for(FactorListener listener : listeners) {
					listener.onContextArrival(context);
				}
			}
		}
	}
	
	public synchronized void subsribeContext(FactorListener listener) {
		this.listeners.add(listener);
	}
	
	public synchronized void unsubsribeContext(FactorListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}
	
	public void subscribe(PrClass plugin, List<String> topics) {
		for (String topic : topics) {
			if(!topicFilterSet.contains(topic)) {
				contextClient.subscribe(topic, contextListener);
				topicFilterSet.add(topic);
			}
		}
		
		pluginContextMap.put(plugin, topics);
		logger.info("Finish subsribe topics " + topics + " for plugn " + plugin.getName() + " in context manager.");
	}
	
	public synchronized ExecutionContext getPluginExecutionContext(PrClass plugin) {
		List<String> subscribedTopics = pluginContextMap.get(plugin);
		Map<String, BaseFactor> pluginContext = new HashMap<String, BaseFactor>();
		for(String topic : subscribedTopics) {
			pluginContext.put(topic, contexts.get(topic));
		}
		return new ExecutionContext(pluginContext);
	}
}
