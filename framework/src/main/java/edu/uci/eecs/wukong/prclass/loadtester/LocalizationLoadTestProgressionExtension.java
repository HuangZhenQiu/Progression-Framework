package edu.uci.eecs.wukong.prclass.loadtester;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.annotation.WuTimer;
import edu.uci.eecs.wukong.framework.api.TimerExecutable;
import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.metrics.Timer;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.property.Location;
import edu.uci.eecs.wukong.framework.property.Response;

public class LocalizationLoadTestProgressionExtension extends AbstractProgressionExtension<LocalizationLoadTester>
	implements Channelable<Response>, TimerExecutable {
	private final static Logger LOGGER = LoggerFactory.getLogger(
			LocalizationLoadTestProgressionExtension.class);
	private Integer seqno;
	private Random random;
	// <sequence, timestamp> Map
	private Map<Integer, Long> seqMap; 

	public LocalizationLoadTestProgressionExtension(LocalizationLoadTester loadTester) {
		super(loadTester);
		this.seqno = 0;
		this.random = new Random();
		this.seqMap = new HashMap<Integer, Long> ();
	}
	
	@WuTimer(interval = 0.01F)
	public void execute() {
		Location location = new Location(random.nextFloat(), random.nextFloat(), random.nextFloat());
		synchronized (seqno) {
			location.setSequence(seqno);
			seqMap.put(seqno, System.currentTimeMillis());
		}
		this.prClass.setLocation(location);
	}

	@Override
	public void execute(ChannelData<Response> data) {
		LOGGER.info("Received response from localization prclass");
		if (seqMap.containsKey(data.getValue().getSequence())) {
			long timestamp = seqMap.get(data.getValue().getSequence());
			Timer timer = this.getPrClass().getPrClassMetrics().getTimer(this.prClass, this);
			long responseTime = System.currentTimeMillis() - timestamp;
			timer.update(responseTime);
			LOGGER.info("Response time of localization prclass is: " + responseTime);
		}
	}

}
