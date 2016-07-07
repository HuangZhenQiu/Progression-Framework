package edu.uci.eecs.wukong.framework.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;
import edu.uci.eecs.wukong.framework.util.Configuration;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;

import java.util.List;

public class FeatureExtractionExtensionPoint extends ExtensionPoint<FeatureExtractionExtension<? extends EdgePrClass>> {
	private static Logger logger = LoggerFactory.getLogger(ExecutionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private FeatureChoosers featureChoosers; 
	
	public FeatureExtractionExtensionPoint (FeatureChoosers featureChoosers, Pipeline pipeline) {
		super(pipeline);
		this.featureChoosers = featureChoosers;
	}

	@Override
	public synchronized void register(FeatureExtractionExtension<? extends EdgePrClass> extension) {
		super.register(extension);
		featureChoosers.addFeatureExtractionExtenshion(extension);
		logger.info("Add Feature Extraction Extension for " + extension.getPrClass());
	}
	
	public void run() {
		logger.info("Feature Extraction Extension Point Starts to Run");
		// Round Robin Choose
		while(true) {
			try {
				Thread.sleep(1000);
				for (EdgePrClass prClass : this.extensionMap.keySet()) {
					List<Object> result = featureChoosers.choose(prClass);
					FeatureEntity<Object> entity = new FeatureEntity<Object>(prClass);
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
