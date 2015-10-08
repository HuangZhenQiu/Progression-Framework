package edu.uci.eecs.wukong.framework.xmpp;

import edu.uci.eecs.wukong.framework.client.FactorClient;
import edu.uci.eecs.wukong.framework.client.FactorClientListener;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.util.Configuration;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XMPPFactorClient implements FactorClient {
	private static Logger logger = LoggerFactory.getLogger(XMPPFactorClient.class);
	private final static Configuration systemConfig= Configuration.getInstance(); 
	private static XMPPFactorClient client;
	private ConnectionConfiguration connectionConfig;
	private XMPPConnection connection;
	private PubSubManager manager;
	
	public static synchronized XMPPFactorClient getInstance() {
		if (client == null) {
			client = new XMPPFactorClient();
		}
		return client;
	}
	
	private XMPPFactorClient(){
		connectionConfig = new ConnectionConfiguration(
				systemConfig.getXMPPAddress(),
				Integer.parseInt(systemConfig.getXMPPPort()), systemConfig.getXMPPServerName());
		connectionConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		connection = new XMPPConnection(connectionConfig);
		try {
			//SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			connection.connect();
			connection.login(systemConfig.getXMPPUserName(), systemConfig.getXMPPPassword());
			manager = new PubSubManager(connection);
			logger.info("XMPP client is initialized!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void subscribe(String nodeId, FactorClientListener listener) {
		try {
			if (listener instanceof ItemEventListener) {
				ItemEventListener<PayloadItem<BaseFactor>> itemEventListener = 
						(ItemEventListener<PayloadItem<BaseFactor>>) listener;
				Node eventNode = manager.getNode(nodeId);
				eventNode.addItemEventListener(itemEventListener);
				eventNode.subscribe(connection.getUser());
				logger.info("XMPP client subcribe nodeId: " + nodeId);
			} else {
				logger.info("Fail to subscribe a topic with a listener which is not type of ItemEventListener<PayloadItem<BaseFactor>>");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void publish(String id, BaseFactor context) {
		try {
			LeafNode node = (LeafNode)manager.getNode(id);
			if (node == null) {
				node = createNode(id);
			}
			
			PayloadItem<BaseFactor> item = new PayloadItem<BaseFactor>(context);
			node.publish(item);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private LeafNode createNode(String nodeId) {
		try {
			LeafNode node = manager.createNode(nodeId);
			ConfigureForm form = new ConfigureForm(FormType.submit);
			form.setAccessModel(AccessModel.open);
			form.setDeliverPayloads(true);
			form.setPersistentItems(true);
			form.setPublishModel(PublishModel.open);
			node.sendConfigurationForm(form);
			return node;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
		return null;
	}
}
	
