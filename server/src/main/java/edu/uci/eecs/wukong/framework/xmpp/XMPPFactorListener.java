package edu.uci.eecs.wukong.framework.xmpp;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorClientListener;
import edu.uci.eecs.wukong.framework.factor.FactorListener;

/**
 * XMPP subscribe broker for all of the PrClasses. It processed all of the facor message from xmpp. Extract message payload from
 * FactorExtensionElement and convert to factor object according to the class information.
 */
public class XMPPFactorListener extends FactorClientListener {
	private static Logger logger = LoggerFactory.getLogger(XMPPFactorListener.class);
	public XMPPFactorListener(ConcurrentMap<String, BaseFactor> factors, List<FactorListener> factorListeners) {
		super(factors, factorListeners);
	}
} 