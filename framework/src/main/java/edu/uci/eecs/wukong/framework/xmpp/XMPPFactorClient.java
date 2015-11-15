package edu.uci.eecs.wukong.framework.xmpp;

import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorClient;
import edu.uci.eecs.wukong.framework.factor.FactorClientListener;
import edu.uci.eecs.wukong.framework.util.Configuration;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.xdata.packet.DataForm;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMPPFactorClient implements FactorClient {
	private static Logger logger = LoggerFactory.getLogger(XMPPFactorClient.class);
	private final static Configuration systemConfig= Configuration.getInstance(); 
	private static XMPPFactorClient client;
	private XMPPTCPConnectionConfiguration connectionConfig;
	private XMPPTCPConnection tcpConnection;
	private PubSubManager manager;
	
	public static synchronized XMPPFactorClient getInstance() {
		if (client == null) {
			client = new XMPPFactorClient();
		}
		return client;
	}
	
	private XMPPFactorClient(){
		try {
			DomainBareJid serviceName = JidCreate.domainBareFrom(systemConfig.getXMPPServerName());
			connectionConfig = XMPPTCPConnectionConfiguration.builder()
				.setUsernameAndPassword(systemConfig.getXMPPUserName(), systemConfig.getXMPPPassword())
				.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
				.setHost(systemConfig.getXMPPAddress())
				.setXmppDomain(serviceName)
				.setPort(Integer.parseInt(systemConfig.getXMPPPort())).build();
			
			logger.info(systemConfig.getXMPPServerName());
			tcpConnection = new XMPPTCPConnection(connectionConfig);
			tcpConnection.setPacketReplyTimeout(10000);
		 	tcpConnection.connect();
		 	tcpConnection.login();
		 	Roster roster = Roster.getInstanceFor(tcpConnection);
		 	roster.setRosterLoadedAtLogin(false);
			manager = PubSubManager.getInstance(tcpConnection);
			logger.info("Successfully connected with XMPP server:" + systemConfig.getXMPPServerName());
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("Catch Exception");
			logger.error("Fail to create XMPP Client, please check username and password in config");
		}
	}
	
	public void subscribe(String nodeId, FactorClientListener listener) {
		try {
			if (listener instanceof ItemEventListener) {
				ItemEventListener<PayloadItem<BaseFactor>> itemEventListener = 
						(ItemEventListener<PayloadItem<BaseFactor>>) listener;
				Node eventNode = getOrCreateNode (nodeId);
				if (eventNode != null) {
					eventNode.addItemEventListener(itemEventListener);
					eventNode.subscribe(tcpConnection.getUser().asEntityBareJidString());
					logger.info("XMPP client subcribe nodeId: " + nodeId);
				} else {
					logger.info("XMPP client fail to subscribe nodeId: " + nodeId);
				}
			} else {
				logger.info("Fail to subscribe a topic with a listener which is not type of ItemEventListener<PayloadItem<BaseFactor>>");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public LeafNode getOrCreateNode (String id) {
		try {
			LeafNode node = (LeafNode)manager.getNode(id);
			return node;
		} catch (Exception e) {
			logger.info("The node " + id + " is not found in server");
		} 
		return createNode(id);
	}
	
	public void publish(String id, BaseFactor context) {
		try {
			LeafNode node = getOrCreateNode(id);
			if (node != null) {
				PayloadItem<BaseFactor> item = new PayloadItem<BaseFactor>(context);
				node.publish(item);
				logger.info("Published message " + context + " to node " + id);
			} else {
				logger.info("Stop to publish message because not able to create node " + id + " in server");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private LeafNode createNode(String nodeId) {
		try {
			LeafNode node = manager.createNode(nodeId);
			ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
			form.setAccessModel(AccessModel.open);
			form.setDeliverPayloads(true);
			form.setPersistentItems(true);
			form.setPublishModel(PublishModel.open);
			node.sendConfigurationForm(form);
			logger.error("Created a new node: " + nodeId);
			return node;
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("Fail to create node: " + nodeId);
		}
		
		return null;
	}
}
