package edu.uci.eecs.wukong.prclass.ft;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.model.ChannelData;

public class FaultTolerantProgressionExtenson extends AbstractExecutionExtension<FaultTolerantPolicyPrClass> implements
	TimerExecutable, Channelable<Byte> {

	public FaultTolerantProgressionExtenson(FaultTolerantPolicyPrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(ChannelData<Byte> data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@WuTimer(interval = 1)
	public void execute() {
		
	}
}
