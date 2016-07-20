package edu.uci.eecs.wukong.prclass.ft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.model.Component;
import edu.uci.eecs.wukong.framework.model.EndPoint;
import edu.uci.eecs.wukong.framework.model.SensorData;
import edu.uci.eecs.wukong.framework.model.WKPFMessageType;

public class FaultTolerantExecutionExtenson extends AbstractExecutionExtension<FaultTolerantPolicyPrClass> implements
	TimerExecutable, Channelable<SensorData>, Initiable {
	private final static Logger LOGGER = LoggerFactory.getLogger(FaultTolerantExecutionExtenson.class);

	private static final int TIMEOUT = 5; // seconds
	// polling status of devices includes both primary and secondary
	private Map<Long, Boolean> deviceStatus;
	private Set<Long> primaryDevices;
	private Map<Long, Integer> failureCounter;
	private Object locker = new Object();
	public List<ExepectedMessage> expectedMessages;
	
	private static class ExepectedMessage {
		private long networkId;
		private WKPFMessageType type;
		private long deadline;
		
		public ExepectedMessage(long networkId, WKPFMessageType type, long deadline) {
			this.networkId = networkId;
			this.type = type;
			this.deadline = deadline;
		}
	}
	
	
	public FaultTolerantExecutionExtenson(FaultTolerantPolicyPrClass plugin) {
		super(plugin);
	}
	
	@Override
	public boolean init() {
		
		deviceStatus = new HashMap<Long, Boolean> ();
		failureCounter = new HashMap<Long, Integer> ();
		primaryDevices = new HashSet<Long> ();
		expectedMessages = new ArrayList<ExepectedMessage> ();
		
		primaryDevices = this.getPrClass().getPollingTarget();
		// We only need to send heart beat to those component that has replica
		for (Long networkId : primaryDevices) {
			deviceStatus.put(networkId, Boolean.TRUE);
			failureCounter.put(networkId, 0);
		}
		
		return true;
	}

	@Override
	public void execute(ChannelData<SensorData> data) {
		if (data.getType() != null) { // It is from global channel
			if (data.getType().equals(WKPFMessageType.GetDeviceStatusReturn)) {
				if (deviceStatus.containsKey((data.getNpp().getNid()))) {
					LOGGER.info("Received GetDeviceStatusReturn from " + data.getNpp().getNid());
					synchronized(locker) {
						// It it already set to faulty, we need to set it back.
						if (!deviceStatus.get(data.getNpp().getNid())) {
							deviceStatus.put(data.getNpp().getNid(), Boolean.TRUE);
						}
						
						// remove all of the pending message
						Iterator<ExepectedMessage> itr = expectedMessages.listIterator();
						while(itr.hasNext()) {
							ExepectedMessage message = itr.next();
							if (message.networkId == data.getNpp().getNid()
									&& message.type == WKPFMessageType.GetDeviceStatusReturn) {
								expectedMessages.remove(message);
							}
						}
					}
				} else {
					// received device status message from unknown device.
				}
			} else if (data.getType().equals(WKPFMessageType.ChangeLinkReturn)) {
				LOGGER.info("Received ChangeLinkReturn from " + data.getNpp().getNid());
			}
		}
	}

	@Override
	@WuTimer(interval = 5)
	public void execute() {
		
		// Send polling message to all of the target device
		for (Map.Entry<Long, Boolean> entry: deviceStatus.entrySet()) {
			this.getPrClass().getPoller().sendHeartBeatRequest(entry.getKey());
			this.expectedMessages.add(
					new ExepectedMessage(entry.getKey(), WKPFMessageType.GetDeviceStatusReturn,
							System.currentTimeMillis() + TIMEOUT * 1000));
		}
		
		// Check the timeout message update the counter
		synchronized (locker) {
			Iterator<ExepectedMessage> itr = expectedMessages.listIterator();
			while(itr.hasNext()) {
				ExepectedMessage message = itr.next();
				if (message.deadline > System.currentTimeMillis()) {
					expectedMessages.remove(message);
					// Update the status of device to failure
					deviceStatus.put(message.networkId, Boolean.FALSE);
					// Update the counter
					failureCounter.put(message.networkId, failureCounter.get(message.networkId) + 1);
				}
			}
			
			Iterator<Long> nodeIds = failureCounter.keySet().iterator();
			while(nodeIds.hasNext()) {
				Long nodeId = nodeIds.next();
				if (failureCounter.get(nodeId) > 3) {
					List<Component> components = this.prClass.getComponentsPrimaryInNode(nodeId);
					for (Component component : components) {
						EndPoint dest = component.getSecondaryEndPoint();
						List<Link> inLinks = this.prClass.getInLinkOfComponent(component);
						for (Link link: inLinks) {
							
							// Find where is the source node located now
							int targetComponentId = link.getSourceId();
							Component targetComponent =  this.prClass.getComponentById(targetComponentId);
							Long souceNodeId = targetComponent.getPrimaryEndPoint().getNodeId();
							
							// Update the primary endpoint of the failure component to secondary endpoint
							this.prClass.getPoller().changeComponentMap(souceNodeId, component.getId(),
									component.getPrimaryEndPoint().getNodeId(), component.getPrimaryEndPoint().getPortId(),
									component.getSecondaryEndPoint().getNodeId(), component.getSecondaryEndPoint().getPortId());

						}
						
						List<Link> outLinks = this.prClass.getInLinkOfComponent(component);
						
						for (Link link : outLinks) {
							int targetComponentId = link.getDestId();
							Component targetComponent =  this.prClass.getComponentById(targetComponentId);
							Long souceNodeId = targetComponent.getPrimaryEndPoint().getNodeId();
							
							// Update the primary endpoint of the failure component to secondary endpoint
							this.prClass.getPoller().changeComponentMap(souceNodeId, component.getId(),
									component.getPrimaryEndPoint().getNodeId(), component.getPrimaryEndPoint().getPortId(),
									component.getSecondaryEndPoint().getNodeId(), component.getSecondaryEndPoint().getPortId());
						}
					}
				}
			}
		}
	}
}
