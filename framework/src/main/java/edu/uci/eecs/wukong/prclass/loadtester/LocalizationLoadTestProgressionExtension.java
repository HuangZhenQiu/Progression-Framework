package edu.uci.eecs.wukong.prclass.loadtester;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
	
	@WuTimer(interval = 1)
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
		if (seqMap.containsKey(data.getValue().getSequence())) {
			long timestamp = seqMap.get(data.getValue().getSequence());
			Timer timer = this.getPrClass().getPrClassMetrics().getTimer(this.prClass, this);
			timer.update(System.currentTimeMillis() - timestamp);
		}		
	}

}
