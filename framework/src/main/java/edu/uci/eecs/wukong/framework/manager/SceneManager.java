package edu.uci.eecs.wukong.framework.manager;

import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.client.FactorClient;
import edu.uci.eecs.wukong.framework.client.FactorClientListener;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.xmpp.XMPPFactorClient;
import edu.uci.eecs.wukong.framework.xmpp.XMPPFactorListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Scene manager is used for delegate PrClass connecting with external factor resources,
 * such as pub/sub model resource XMPP, MQTT.
 * 
 *
 */
public class SceneManager {
	private static Logger logger = LoggerFactory.getLogger(SceneManager.class);
	private static Gson gson = new Gson();
	private Map<PrClass, List<String>> pluginContextMap;
	private ConcurrentMap<String, BaseFactor> factors;
	private FactorClient factorClient;
	private Set<String> topicFilterSet;
	private FactorClientListener factorClientListener;
	private List<FactorListener> listeners;
	
	public SceneManager() {
		pluginContextMap = new HashMap<PrClass, List<String>>();
		factors = new ConcurrentHashMap<String, BaseFactor>();
		listeners = new ArrayList<FactorListener>();
		factorClient = XMPPFactorClient.getInstance();
		topicFilterSet = new HashSet<String>();
		factorClientListener = new XMPPFactorListener(factors, listeners);
	}
	
	public synchronized void subsribeFactor(FactorListener listener) {
		this.listeners.add(listener);
	}
	
	public synchronized void unsubsribeFactor(FactorListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}
	
	public void subscribe(PrClass plugin, List<String> topics) {
		if (topics != null) {
			for (String topic : topics) {
				if(!topicFilterSet.contains(topic)) {
					factorClient.subscribe(topic, factorClientListener);
					topicFilterSet.add(topic);
				}
			}
			
			pluginContextMap.put(plugin, topics);
			logger.info("Finish subsribe topics " + topics + " for PrClass " + plugin.getName() + " in Scene Manager.");
		} else {
			logger.info("There is no topic to subscribe for for PrClass " + plugin.getName() + " in Scene Manager.");
		}
	}
	
	public void unsubscribe(PrClass plugin) {
		if (pluginContextMap.containsKey(plugin)) {
			pluginContextMap.remove(plugin);
		}
	}
	
	public synchronized ExecutionContext getPluginExecutionContext(PrClass plugin) {
		List<String> subscribedTopics = pluginContextMap.get(plugin);
		Map<String, BaseFactor> factorMap = new HashMap<String, BaseFactor>();
		for(String topic : subscribedTopics) {
			factorMap.put(topic, factors.get(topic));
		}
		return new ExecutionContext(factorMap, factorClient);
	}
}
