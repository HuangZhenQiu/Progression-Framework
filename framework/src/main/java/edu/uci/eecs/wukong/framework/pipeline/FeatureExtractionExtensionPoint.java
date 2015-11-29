package edu.uci.eecs.wukong.framework.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;

import java.util.List;

public class FeatureExtractionExtensionPoint extends ExtensionPoint<FeatureAbstractionExtension> {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private FeatureChoosers featureChoosers; 
	
	public FeatureExtractionExtensionPoint (FeatureChoosers featureChoosers, Pipeline pipeline) {
		super(pipeline);
		this.featureChoosers = featureChoosers;
	}

	@Override
	public synchronized void register(FeatureAbstractionExtension extension) {
		super.register(extension);
		featureChoosers.addFeatureExtractionExtenshion(extension);
	}
	
	public void run() {
		// Round Rubin Choose
		for (PrClass prClass : this.extensionMap.keySet()) {
			try {
				List<Number> result = featureChoosers.choose(prClass);
				FeatureEntity<Number> entity = new FeatureEntity<Number>(prClass);
				entity.addFeatures(result);
				this.send(entity);
			} catch (Exception e) {
				logger.info("Fail to choose features for PrClass " + prClass);
			}
		}
	}
}
