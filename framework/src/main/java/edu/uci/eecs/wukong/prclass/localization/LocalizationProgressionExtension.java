package edu.uci.eecs.wukong.prclass.localization;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;
import edu.uci.eecs.wukong.framework.api.metrics.Timer;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.localization.ParticleFilter;
import edu.uci.eecs.wukong.framework.localization.Map;

public class LocalizationProgressionExtension extends AbstractProgressionExtension<LocalizationPrClass> implements
	Channelable<Short>, FactorExecutable {
	private ParticleFilter filter;
	private Map map;
	private boolean inited = false;
	
	public LocalizationProgressionExtension(LocalizationPrClass plugin) {
		super(plugin);
	}
	

	@Override
	public void execute(ChannelData<Short> data) {
		Timer timer = this.prClass.getPrClassMetrics().getTimer(this.prClass, this);
		if (inited) {
			long start = System.currentTimeMillis();
			
			// Do somthing		
		    long end = System.currentTimeMillis();
			timer.update(end - start);
		}	
	}


	@Override
	public void execute(BaseFactor context) {
		if (context instanceof MapFactor) {
			this.map = ((MapFactor) context).getMap();
			this.filter = new ParticleFilter(map, this.prClass.getParticleCount(),
					this.prClass.getSensors() , this.prClass.getMovNoise(),
					this.prClass.getRotNoise(), this.prClass.getSenseNoise(), this.prClass.getMaxr());
			this.inited = true;
		}
	}
}
