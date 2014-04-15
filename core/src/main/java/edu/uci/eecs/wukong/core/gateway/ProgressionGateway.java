package edu.uci.eecs.wukong.core.gateway;

import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.network.netty.CommunicationClient;
import edu.uci.eecs.wukong.network.netty.service.ProgressionDataServiceClient;

/***
 * 
 * LLama Agent is a very important component of LLama Cloud ecosystem. We will provision a dedicated server
 * for each data center, then place an agent within the server. Basically it has these functionalities:
 * 
 * 1) Aggregation the data from collectors for each application.
 * 2) Accept the commands from LLama Cloud Server, and then trigger
 *    the right auto-scaling actions for particular application
 * 3) Keep tracking the heart beat with the server and the collectors within the data center.
 * 
 * @author huangzhenqiu0825
 *
 */
public class ProgressionGateway {
	
	private static Logger logger = LoggerFactory.getLogger(ProgressionGateway.class);
	
	//It is the client that talk to LLama Cloud Server
	private CommunicationClient communicationClient;
	private ProgressionDataServiceClient dataServiceClient;
	private RpcClientChannel channel;
	
	public ProgressionGateway() {
		
		//move to configuration
		PeerInfo server = new PeerInfo("localhost", 100);
		PeerInfo client = new PeerInfo("localhost", 101);
		communicationClient = new CommunicationClient(server, client, false, false, false);
		
		try {
			channel = communicationClient.start();
		} catch(Exception e) {
			logger.error("Agent can't connect with LLama Cloud Server.");
		}
		
		dataServiceClient = new ProgressionDataServiceClient(channel);
	}
	
	
	public void shutdown() {
		//TODO huangzhenqiu0825 
	}

	public void main(String[] args) {
		ProgressionGateway agent = new ProgressionGateway();
	}
}
