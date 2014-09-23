package edu.uci.eecs.wukong.rpc.netty.service;

import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;

import edu.uci.eecs.wukong.rpc.netty.service.DataService.BlockingDataService;
import edu.uci.eecs.wukong.rpc.netty.service.DataService.Datagram;
import edu.uci.eecs.wukong.rpc.netty.service.DataService.Feedback;
import edu.uci.eecs.wukong.rpc.netty.service.DataService.NonBlockingDataService;

//TODO Chen Liu
public class ProgressionDataServiceFactory{

	 public static class BlockingDataServiceImpl implements BlockingDataService.BlockingInterface {
		 
		 public Feedback send(RpcController controller, Datagram request)
		          throws ServiceException{
			 
			 Feedback.Builder builder = Feedback.newBuilder();
			 builder.setReturnId(request.getServiceId() + 1);
			 
			 return builder.build();
		 }
	 }
	 
     public static class NonblockingDataServiceImpl implements NonBlockingDataService.Interface {
    	 
		 public void send(RpcController controller, Datagram request,
				 RpcCallback<Feedback> done){
			 
			 Feedback.Builder builder = Feedback.newBuilder();
			 builder.setReturnId(request.getServiceId() + 1);
			 
			 done.run(builder.build());
		 }
     }

}
