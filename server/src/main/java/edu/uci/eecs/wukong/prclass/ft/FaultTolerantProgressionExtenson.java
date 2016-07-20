package edu.uci.eecs.wukong.prclass.ft;

import java.util.HashMap;
import java.util.Map;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.model.SensorData;
import edu.uci.eecs.wukong.framework.model.WKPFMessageType;

public class FaultTolerantProgressionExtenson extends AbstractExecutionExtension<FaultTolerantPolicyPrClass> implements
	TimerExecutable, Channelable<SensorData>, Initiable {
	private Map<Long, Boolean> deviceStatus;
	private Map<Long, Integer> failureCounter;
	private Object locker = new Object();

	public FaultTolerantProgressionExtenson(FaultTolerantPolicyPrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean init() {
		deviceStatus = new HashMap<Long, Boolean> ();
		failureCounter = new HashMap<Long, Integer> ();
		for (Long address : this.getPrClass().getAllComponentAddress()) {
			deviceStatus.put(address, Boolean.TRUE);
			failureCounter.put(address, 0);
		}
		
		return true;
	}

	@Override
	public void execute(ChannelData<SensorData> data) {
		if (data.getType() != null) { // It is from global channel
			if (data.getType().equals(WKPFMessageType.GetDeviceStatusReturn)) {
				if (deviceStatus.containsKey((data.getNpp().getNid()))) {
					synchronized(locker) {
						if (deviceStatus.get(data.getNpp().getNid())) {
							
						} else {
							
						}
					}
				} else {
					// received device status message from unknown device.
				}
			} else if (data.getType().equals(WKPFMessageType.ChangeLinkReturn)) {
			}
		}
	}

	@Override
	@WuTimer(interval = 5)
	public void execute() {
		for (Map.Entry<Long, Boolean> entry: deviceStatus.entrySet()) {
			// if it is a healthy node, send out request
			if (entry.getValue()) {
				this.getPrClass().getPoller().sendHeartBeatRequest(entry.getKey());
			} else {
				// accumulate failure counter
				synchronized(locker) {
					int current = failureCounter.get(entry.getKey()) + 1;
					failureCounter.put(entry.getKey(), current);
					
					if (current > 3) {
						// Start to change link to find the component in the node.
					}
				}
			}
		}
	}
}
