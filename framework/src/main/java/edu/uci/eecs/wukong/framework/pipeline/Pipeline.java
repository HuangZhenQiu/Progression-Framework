package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.manager.ContextManager;
import edu.uci.eecs.wukong.framework.extension.Extension;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;
import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;

import java.util.List;
import java.lang.Thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class Pipeline {
	private final static Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);
	private ContextManager contextManager;
	private ConfigurationManager configurationManager;
	private FeatureAbstractionPoint featureAbstractionPoint;
	private ProgressionExtensionPoint progressionPoint;
	private LearningPoint learningPoint;
	
	@VisibleForTesting
	public Pipeline() {
		
	}
	
	public Pipeline(ContextManager contextManager,
			ConfigurationManager configuraionManager, BufferManager bufferManager) {
		this.contextManager = contextManager;
		this.configurationManager = configuraionManager;
		this.progressionPoint = new ProgressionExtensionPoint(configuraionManager, this);
		this.featureAbstractionPoint = new FeatureAbstractionPoint(bufferManager, this);
		this.learningPoint = new LearningPoint(this);
		this.contextManager.subsribeContext(progressionPoint);
	}
	
	public void registerExtension(List<Extension> extensions) {
		for (Extension extension : extensions) {
			if (extension instanceof ProgressionExtension) {
				progressionPoint.register((ProgressionExtension)extension);
			}
			
			if (extension instanceof FeatureAbstractionExtension) {
				featureAbstractionPoint.register((FeatureAbstractionExtension) extension);
			}
			
			if (extension instanceof LearningExtension) {
				learningPoint.register((LearningExtension) extension);
			}
		}
	}
	
	public void start() {
		Thread featureAbstraction = new Thread(featureAbstractionPoint);
		Thread learning = new Thread(learningPoint);
		Thread progression = new Thread(progressionPoint);
		featureAbstraction.start();
		learning.start();
		progression.start();
		LOGGER.info("Progression Pipeline get started.");
	}
}
