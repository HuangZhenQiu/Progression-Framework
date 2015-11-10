package edu.uci.eecs.wukong.framework.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;

public class FeatureAbstractionExtensionPoint extends ExtensionPoint<FeatureAbstractionExtension> implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private FeatureChoosers featureChoosers; 
	
	public FeatureAbstractionExtensionPoint (FeatureChoosers featureChoosers, Pipeline pipeline) {
		super(pipeline);
		this.featureChoosers = featureChoosers;
	}

	@Override
	public synchronized void register(FeatureAbstractionExtension extension) {
		super.register(extension);
		featureChoosers.addFeatureExtractionExtenshion(extension);
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
