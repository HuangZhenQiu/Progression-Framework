package edu.uci.eecs.wukong.framework.xmpp;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorClientListener;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.prclass.demo.DemoFactor;
import edu.uci.eecs.wukong.prclass.icsdemo.ICSContext;

public class XMPPFactorListener extends FactorClientListener implements ItemEventListener<PayloadItem<BaseFactor>> {
	private static Logger logger = LoggerFactory.getLogger(XMPPFactorListener.class);
	public XMPPFactorListener(ConcurrentMap<String, BaseFactor> factors, List<FactorListener> factorListeners) {
		super(factors, factorListeners);
	}

	public void handlePublishedItems(ItemPublishEvent evt){
		for (Object object :evt.getItems()) {
			PayloadItem item = (PayloadItem)object;
			String message = item.getPayload().toString();
			int start = message.indexOf('{');
			int end = message.indexOf('}');
			ICSContext factor = gson.fromJson(message.substring(start, end + 1), ICSContext.class);
			factors.put(factor.getTopicId(), factor);
			for(FactorListener listener : this.factorListeners) {
				listener.onFactorArrival(factor);
			}
		}
	}
} 