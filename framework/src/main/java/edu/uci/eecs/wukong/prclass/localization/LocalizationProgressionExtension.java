package edu.uci.eecs.wukong.prclass.localization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;
import edu.uci.eecs.wukong.framework.api.metrics.Timer;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.test.LoadGenerator.Location;
import edu.uci.eecs.wukong.framework.localization.ParticleFilter;
import edu.uci.eecs.wukong.framework.localization.Map;

public class LocalizationProgressionExtension extends AbstractProgressionExtension<LocalizationPrClass> implements
	Channelable<Location>, FactorExecutable {
	private final static Logger LOGGER = LoggerFactory.getLogger(LocalizationProgressionExtension.class);
	private ParticleFilter filter;
	private Map map;
	private boolean inited = false;
	
	public LocalizationProgressionExtension(LocalizationPrClass plugin) {
		super(plugin);
	}

	@Override
	public void execute(ChannelData<Location> data) {
		try {
			Timer timer = this.prClass.getPrClassMetrics().getTimer(this.prClass, this);
			long start = System.currentTimeMillis();
			// Do something
			Thread.sleep(2000);
			long end = System.currentTimeMillis();
			timer.update(end - start);
		} catch (Exception e) {
			LOGGER.error("Fail to execut method triggered by channel: " + e.toString());
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
