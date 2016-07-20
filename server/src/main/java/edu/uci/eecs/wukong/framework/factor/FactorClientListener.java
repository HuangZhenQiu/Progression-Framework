package edu.uci.eecs.wukong.framework.factor;

import java.io.StringReader;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import edu.uci.eecs.wukong.framework.xmpp.FactorExtensionElement;

/**
 * FactorClientListener is base class for integrating listeners of every type of pub/sub system.
 * Once receive any message from the source. It is required to add those messages into the 
 * global ConcurrentMap factors. It is the unique map thats stores the latest factor of each topic.
 * A ExecutionContext instance need to pick up factor values from the map.
 */
public abstract class FactorClientListener implements ItemEventListener<PayloadItem<SimplePayload>>{
	private static Logger logger = LoggerFactory.getLogger(FactorClientListener.class);
	protected static Gson gson = new Gson();
    /* Shared concurrent data structure of all FactorClientListener instances */
	protected ConcurrentMap<String, BaseFactor> factors;
	/* Shared listeners with scene manager */
	protected List<FactorListener> factorListeners;
	
	public FactorClientListener(ConcurrentMap<String, BaseFactor> factors, List<FactorListener> factorListeners) {
		this.factors = factors;
		this.factorListeners = factorListeners;
	}
	
	public void register(FactorListener listener) {
		this.factorListeners.add(listener);
	}
	
	public void handlePublishedItems(ItemPublishEvent<PayloadItem<SimplePayload>> evt){
		for (PayloadItem<SimplePayload> object :evt.getItems()) {
			SimplePayload message = object.getPayload();
			if (message instanceof SimplePayload) {
				String factorXML = message.toXML().toString();
				try {
					JAXBContext jaxbContext = JAXBContext.newInstance(FactorExtensionElement.class);
					Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
					XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		            XMLStreamReader reader = inputFactory.createXMLStreamReader(new StringReader(factorXML));
		            FactorExtensionElement element = jaxbUnmarshaller.unmarshal(reader, FactorExtensionElement.class).getValue();
		            if (element.getContent() != null) {
						Class<BaseFactor> factorClass = (Class<BaseFactor>) Class.forName(element.getClassName());
						BaseFactor factor = gson.fromJson(element.getContent(), factorClass);
						logger.info("received factor from xmpp sent time is " + factor.getTimestamp());
						factors.put(factor.getTopicId(), factor);
						for(FactorListener listener : this.factorListeners) {
							listener.onFactorArrival(factor);
						}
		            }
				} catch (ClassNotFoundException e) {
					logger.error("Can't find load factor class to interprete the message content: " + factorXML);
				} catch (JAXBException jaxException) {
					logger.error("Can't use JAXB " + jaxException.toString());
				} catch (XMLStreamException xmlException) {
					logger.error("fail to parse xml stream " + factorXML);
				}
			}
		}
	}
}
