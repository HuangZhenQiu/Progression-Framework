package edu.uci.eecs.wukong.framework.policy.energy;

import java.util.Iterator;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.model.Link;
import edu.uci.eecs.wukong.framework.policy.energy.EnergyPolicyPrClass;

public class EnergyPolicyProgressionExtension extends AbstractExecutionExtension<EnergyPolicyPrClass> implements
	TimerExecutable {

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
				this.prClass.sendGetLinkCounter(linkSourceAddress, i++);
			}
		}
	}
}
