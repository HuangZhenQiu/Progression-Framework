package edu.uci.eecs.wukong.framework.factor;

import com.google.gson.Gson;
import com.google.common.annotations.VisibleForTesting;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
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
 */
public class SceneManager {
	private static Logger logger = LoggerFactory.getLogger(SceneManager.class);
	private static Gson gson = new Gson();
	private static String TEST_FACTOR = "test";
	private Map<EdgePrClass, List<String>> pluginContextMap;
	private ConcurrentMap<String, BaseFactor> factors;
	private FactorClient factorClient;
	private Set<String> topicFilterSet;
	private FactorClientListener factorClientListener;
	private List<FactorListener> listeners;
	private FactorMetrics metrics;
	
	@VisibleForTesting
	public SceneManager() {
		this(null);
	}

	public SceneManager(FactorMetrics metrics) {
		this.pluginContextMap = new HashMap<EdgePrClass, List<String>>();
		this.factors = new ConcurrentHashMap<String, BaseFactor>();
		this.listeners = new ArrayList<FactorListener>();
		this.factorClient = FactorClientFactory.getFactorClient();
		this.topicFilterSet = new HashSet<String>();
		this.factorClientListener = new XMPPFactorListener(factors, listeners);
		this.metrics = metrics;
		subscribeTestFactor();
	}
	
	public synchronized void subsribeFactor(FactorListener listener) {
		this.listeners.add(listener);
	}
	
	public synchronized void unsubsribeFactor(FactorListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}
	
	public void subscribeTestFactor() {
		factorClient.subscribe(TEST_FACTOR, factorClientListener);
	}
	
	public void subscribe(EdgePrClass plugin, List<String> topics) {
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
	
	public void unsubscribe(EdgePrClass plugin) {
		if (pluginContextMap.containsKey(plugin)) {
			pluginContextMap.remove(plugin);
		}
	}
	
	public synchronized ExecutionContext getPluginExecutionContext(EdgePrClass plugin) {
		List<String> subscribedTopics = pluginContextMap.get(plugin);
		Map<String, BaseFactor> factorMap = new HashMap<String, BaseFactor>();
		if (subscribedTopics != null) {
			for(String topic : subscribedTopics) {
				if (factors.containsKey(topic)) {
					factorMap.put(topic, factors.get(topic));
				}
			}
		}
		return new ExecutionContext(factorMap, factorClient);
	}
}
