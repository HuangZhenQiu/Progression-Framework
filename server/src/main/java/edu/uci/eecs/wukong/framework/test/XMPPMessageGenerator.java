package edu.uci.eecs.wukong.framework.test;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.xmpp.XMPPFactorClient;
import edu.uci.eecs.wukong.framework.xmpp.XMPPFactorListener;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorClientListener;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.xmpp.FactorExtensionElement;

public class XMPPMessageGenerator {
	private final static Configuration systemConfig = Configuration.getInstance(); 
	private static String TEST_TOPIC = "test";
	private XMPPFactorClient client;
	private TestListener listener;
	
	private static class TestFactor extends BaseFactor {
		private String content; 
		public TestFactor(String content) {
			super(TEST_TOPIC);
			this.content = content;
		}
	}
	
	private static class TestListener extends FactorClientListener {
		private static Logger logger = LoggerFactory.getLogger(XMPPFactorListener.class);
		public TestListener(ConcurrentMap<String, BaseFactor> factors, List<FactorListener> factorListeners) {
			super(factors, factorListeners);
		}

		public void handlePublishedItems(ItemPublishEvent evt){
			System.out.println("Received event");
			for (Object object :evt.getItems()) {
				PayloadItem item = (PayloadItem)object;
				String message = item.getPayload().toString();
				System.out.println(message);
			}
		}
	}
	
	public XMPPMessageGenerator() {
		client = new XMPPFactorClient(systemConfig.getXMPPTestUserName(), systemConfig.getXMPPTestPassword());
		listener = new TestListener(null, null);
	}
	
	public void publish(String key, BaseFactor context) {
		client.publish(key, context);
	}
	
	public void subscribe(String key) {
		client.subscribe(key, listener);
	}
	
	public static void main(String[] args) {
		XMPPMessageGenerator generater = new XMPPMessageGenerator();
		generater.subscribe(TEST_TOPIC);
		try {
			for (int i = 0; i < 100; i++) {
				TestFactor factor = new TestFactor("ttt" + i);
				generater.publish(factor.getTopicId(), factor);
				Thread.sleep(10000);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
} 
