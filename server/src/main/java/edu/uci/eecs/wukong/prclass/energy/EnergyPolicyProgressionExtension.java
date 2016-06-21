package edu.uci.eecs.wukong.prclass.energy;

import java.util.Iterator;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.prclass.energy.EnergyPolicyPrClass;

public class EnergyPolicyProgressionExtension extends AbstractExecutionExtension<EnergyPolicyPrClass> implements
	TimerExecutable, Channelable<Integer> {

	public EnergyPolicyProgressionExtension(EnergyPolicyPrClass plugin) {
		super(plugin);
	}

	@WuTimer(interval = 1)
	public void execute() {
		Iterator<Link> iterator = this.getPrClass().getLinkIterator();
		short i = 0;
		while (iterator.hasNext()) {
			Link link = iterator.next();
			Long linkSourceAddress = this.prClass.getComponentAddress(link.getSourceId());
			if (linkSourceAddress != -1) {
				this.getPrClass().getPoller().sendGetLinkCounter(linkSourceAddress, i++);
			}
		}
	}

	@Override
	public void execute(ChannelData<Integer> data) {
		// TODO Auto-generated method stub
		
	}

}
