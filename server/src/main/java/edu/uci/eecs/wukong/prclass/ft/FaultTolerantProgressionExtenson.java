package edu.uci.eecs.wukong.prclass.ft;

import java.util.List;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.model.SensorData;
import edu.uci.eecs.wukong.framework.model.WKPFMessageType;

public class FaultTolerantProgressionExtenson extends AbstractExecutionExtension<FaultTolerantPolicyPrClass> implements
	TimerExecutable, Channelable<SensorData> {

	public FaultTolerantProgressionExtenson(FaultTolerantPolicyPrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(ChannelData<SensorData> data) {
		if (data.getType() != null) { // It is from global channel
			if (data.getType().equals(WKPFMessageType.GetDeviceStatusReturn)) {
				
			}
			
		}
	}

	@Override
	@WuTimer(interval = 1)
	public void execute() {
		List<Long> deviceAddresses = this.getPrClass().getAllComponentAddress();
	}
}
