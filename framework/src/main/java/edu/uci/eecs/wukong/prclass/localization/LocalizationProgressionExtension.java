package edu.uci.eecs.wukong.prclass.localization;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.api.metrics.Timer;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.property.Location;
import edu.uci.eecs.wukong.framework.localization.ParticleFilter;
import edu.uci.eecs.wukong.framework.localization.Map;

public class LocalizationProgressionExtension extends AbstractProgressionExtension<LocalizationPrClass> implements
	Channelable<Location>, FactorExecutable, Initiable {
	private final static Logger LOGGER = LoggerFactory.getLogger(LocalizationProgressionExtension.class);
	private ParticleFilter filter;
	private Map map;
	private boolean inited = false;
	
	public LocalizationProgressionExtension(LocalizationPrClass plugin) {
		super(plugin);

	}
	
	@Override
	public void init() {
		boolean[][] values = new boolean[100][100];
		for (int i = 0; i < values.length; i ++) {
			values[i] = new boolean[100];
			Arrays.fill(values[i], true);
		}
		
		map = new Map(values, 9, 9);
		this.filter = new ParticleFilter(map, this.prClass.getParticleCount(),
				this.prClass.getSensors() , this.prClass.getMovNoise(),
				this.prClass.getRotNoise(), this.prClass.getSenseNoise(), this.prClass.getMaxr());	
	}

	@Override
	public void execute(ChannelData<Location> data) {
		try {
			Timer timer = this.prClass.getPrClassMetrics().getTimer(this.prClass, this);
			long start = System.currentTimeMillis();
			double[] sensorValues = new double[3];
			sensorValues[0] = data.getValue().getX();
			sensorValues[1] = data.getValue().getY();
			sensorValues[2] = data.getValue().getZ();
			filter.step(sensorValues, 1, 1, 2);
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
