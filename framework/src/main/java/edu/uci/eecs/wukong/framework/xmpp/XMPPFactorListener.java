package edu.uci.eecs.wukong.framework.xmpp;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

import edu.uci.eecs.wukong.framework.client.FactorClientListener;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.prclass.demo.DemoFactor;

public class XMPPFactorListener extends FactorClientListener implements ItemEventListener<PayloadItem<BaseFactor>> {
	
	public XMPPFactorListener(ConcurrentMap<String, BaseFactor> factors, List<FactorListener> factorListeners) {
		super(factors, factorListeners);
	}

	public void handlePublishedItems(ItemPublishEvent evt){
		for (Object object :evt.getItems()) {
			PayloadItem item = (PayloadItem)object;
			String message = item.getPayload().toString();
			int start = message.indexOf('{');
			int end = message.indexOf('}');
			DemoFactor factor = gson.fromJson(message.substring(start, end + 1), DemoFactor.class);
			factors.put(factor.getTopicId(), factor);
			for(FactorListener listener : this.factorListeners) {
				listener.onFactorArrival(factor);
			}
		}
	}
} 