package edu.uci.eecs.wukong.prclass.switcher;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Channelable;
import edu.uci.eecs.wukong.framework.api.FactorExecutable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.UserFactor;
import edu.uci.eecs.wukong.framework.model.ChannelData;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;

public class SwitchProgressionExtension extends AbstractProgressionExtension
	implements FactorExecutable, Channelable {
	private static Logger logger = LoggerFactory.getLogger(SwitchProgressionExtension.class);
	// User ID to threshold Map
	private Map<String, Double> userThresholdMap;
	
	public SwitchProgressionExtension(PipelinePrClass plugin){
		super(plugin);
		this.userThresholdMap = new HashMap<String, Double>();
		//Add predefined rules map here.
	}
	
	public void activate(Object model) {
		userThresholdMap.putAll((Map<String, Double>) model);
	}
	
	public void execute(ChannelData data) { 
		logger.info("Received channel input: " + data);
	}
	
	public void execute(BaseFactor context) {
		if (context instanceof UserFactor) {
			UserFactor userContext = (UserFactor)context;
			Double threshold = userThresholdMap.get(userContext.getUid());
			if(threshold != null) {	
				SwitchPrClass plugin =  (SwitchPrClass)this.getPrClass();
				plugin.setThreshold(threshold);
			}
		}
	}
}
