package edu.uci.eecs.wukong.framework.client;

import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.util.Configuration;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
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
public class XMPPContextClient {
	private static Logger logger = LoggerFactory.getLogger(XMPPContextClient.class);
	private final static Configuration systemConfig= Configuration.getInstance(); 
	private static XMPPContextClient client;
	private ConnectionConfiguration connectionConfig;
	private XMPPConnection connection;
	private PubSubManager manager;
	
	public static synchronized XMPPContextClient getInstance() {
		if (client == null) {
			client = new XMPPContextClient();
		}
		return client;
	}
	
	private XMPPContextClient(){
		connectionConfig = new ConnectionConfiguration(
				systemConfig.getXMPPAddress(), Integer.parseInt(systemConfig.getXMPPPort()));
		connection = new XMPPConnection(connectionConfig);
		try {
			SASLAuthentication.supportSASLMechanism("PLAIN", 0);
			connection.connect();
			connection.login(systemConfig.getXMPPUserName(), systemConfig.getXMPPPassword());
			manager = new PubSubManager(connection, systemConfig.getXMPPServerName());
			logger.info("XMPP client is initialized!");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void subscribe(String nodeId, ItemEventListener<PayloadItem<Context>> listener) {
		try {
			Node  eventNode = manager.getNode(nodeId);
			eventNode.addItemEventListener(listener);
			eventNode.subscribe(connection.getUser());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void publish(String id, Context context) {
		try {
			LeafNode node = manager.getNode(id);
			if (node == null) {
				node = createNode(id);
			}
			
			PayloadItem<Context> item = new PayloadItem<Context>(context);
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
		}
		
		return null;
	}
}
	
