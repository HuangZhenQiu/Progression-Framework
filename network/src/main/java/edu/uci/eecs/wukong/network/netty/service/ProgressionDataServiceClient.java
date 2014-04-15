package edu.uci.eecs.wukong.network.netty.service;

import java.util.Vector;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.ClientRpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;


import edu.uci.eecs.wukong.network.netty.service.DataService.Datagram;
import edu.uci.eecs.wukong.network.netty.service.DataService.Feedback;
import edu.uci.eecs.wukong.network.netty.service.DataService.BlockingDataService;
import edu.uci.eecs.wukong.network.netty.service.DataService.NonBlockingDataService;


public class ProgressionDataServiceClient {
	
	private RpcClientChannel channel;
	private BlockingDataService.BlockingInterface blockingInterface;
	private NonBlockingDataService.Interface nonBlockinginterface;
	
	
	public ProgressionDataServiceClient(RpcClientChannel channel) {
		this.channel = channel;
		this.blockingInterface = BlockingDataService.newBlockingStub(channel);
		this.nonBlockinginterface = NonBlockingDataService.newStub(channel);
	}
	
	
	public Feedback send(int serviceId, Vector<Integer> data) throws ServiceException {
		final ClientRpcController controller = channel.newRpcController();
		Datagram.Builder dataBuilder = Datagram.newBuilder();
		dataBuilder.setServiceId(serviceId);
		dataBuilder.addAllData(data);
		
		//TODO Liuchen  setting the controller and build the request from parameters. 
		return blockingInterface.send(controller, dataBuilder.build());
	}
	
	
	public void send(int serviceId, Vector<Integer> data, RpcCallback<Feedback> callback) {
		
		final ClientRpcController controller = channel.newRpcController();
		Datagram.Builder dataBuilder = Datagram.newBuilder();
		dataBuilder.setServiceId(serviceId);
		dataBuilder.addAllData(data);
		
		//TODO Liuchen  setting the controller and build the request from parameters. 
		nonBlockinginterface.send(controller, dataBuilder.build(), callback);
	}

}
