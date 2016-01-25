package edu.uci.eecs.wukong.framework.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;

import java.util.List;

public class FeatureExtractionExtensionPoint extends ExtensionPoint<FeatureExtractionExtension<? extends PipelinePrClass>> {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private FeatureChoosers featureChoosers; 
	
	public FeatureExtractionExtensionPoint (FeatureChoosers featureChoosers, Pipeline pipeline) {
		super(pipeline);
		this.featureChoosers = featureChoosers;
	}

	@Override
	public synchronized void register(FeatureExtractionExtension<? extends PipelinePrClass> extension) {
		super.register(extension);
		featureChoosers.addFeatureExtractionExtenshion(extension);
		logger.info("Add Feature Extraction Extension for " + extension.getPrClass());
	}
	
	public void run() {
		logger.info("Feature Extraction Extension Point Starts to Run");
		// Round Rubin Choose
		while(true) {
			try {
				Thread.sleep(10000);
				for (PipelinePrClass prClass : this.extensionMap.keySet()) {
					List<Number> result = featureChoosers.choose(prClass);
					FeatureEntity<Number> entity = new FeatureEntity<Number>(prClass);
					entity.addFeatures(result);
					this.send(entity);
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("Fail to choose features for PrClasses");
			}
			
		}
	}
}
