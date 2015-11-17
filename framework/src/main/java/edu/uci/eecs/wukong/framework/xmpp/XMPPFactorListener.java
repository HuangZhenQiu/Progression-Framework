package edu.uci.eecs.wukong.framework.xmpp;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorClientListener;
import edu.uci.eecs.wukong.framework.factor.FactorListener;

public class XMPPFactorListener extends FactorClientListener implements ItemEventListener<PayloadItem<FactorExtensionElement>> {
	private static Logger logger = LoggerFactory.getLogger(XMPPFactorListener.class);
	public XMPPFactorListener(ConcurrentMap<String, BaseFactor> factors, List<FactorListener> factorListeners) {
		super(factors, factorListeners);
	}

	public void handlePublishedItems(ItemPublishEvent<PayloadItem<FactorExtensionElement>> evt){
		for (PayloadItem<FactorExtensionElement> object :evt.getItems()) {
			ExtensionElement message = object.getPayload();
			if (message instanceof FactorExtensionElement) {
				FactorExtensionElement element = (FactorExtensionElement) message;
				try {
					Class<BaseFactor> factorClass = (Class<BaseFactor>) Class.forName(element.getClassName());
					BaseFactor factor = gson.fromJson(element.getContent(), factorClass);
					factors.put(factor.getTopicId(), factor);
					for(FactorListener listener : this.factorListeners) {
						listener.onFactorArrival(factor);
					}
				} catch (ClassNotFoundException e) {
					logger.error("Can't find load factor class to interprete the message content: " + element.toXML());
				}
			}
		}
	}
} 