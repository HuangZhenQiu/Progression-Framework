package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.manager.ContextManager;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;

import java.util.List;
import java.lang.Thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class Pipeline implements FactorListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);
	private ContextManager contextManager;
	private ConfigurationManager configurationManager;
	private BufferManager bufferManager;
	private FeatureChoosers featureChoosers;
	private FeatureAbstractionExtensionPoint featureAbstractionPoint;
	private ProgressionExtensionPoint progressionPoint;
	private LearningExtensionPoint learningPoint;
	private ExecutionContext executionContext;
	
	@VisibleForTesting
	public Pipeline() {
		
	}
	
	public Pipeline(ContextManager contextManager,
			ConfigurationManager configuraionManager, FeatureChoosers featureChoosers) {
		this.contextManager = contextManager;
		this.configurationManager = configuraionManager;
		this.featureChoosers = featureChoosers;
		this.progressionPoint = new ProgressionExtensionPoint(configuraionManager, this);
		this.featureAbstractionPoint = new FeatureAbstractionExtensionPoint(featureChoosers, this);
		this.learningPoint = new LearningExtensionPoint(this);
		this.contextManager.subsribeContext(learningPoint);
		this.contextManager.subsribeContext(this);
		this.executionContext = new ExecutionContext();
	}
	
	public ExecutionContext getCurrentContext(PrClass plugin) {
		ExecutionContext context = new ExecutionContext();
		for (String topicId : plugin.registerContext()) {
			BaseFactor cont = executionContext.getContext(topicId);
			if (cont != null) {
				context.addContext(cont);
			}
		}
		
		return context;
	}
	
	public void dipatchModel(String appId, Object model) throws Exception {
		progressionPoint.applyModel(appId, model);
	}
	
	public void registerExtension(List<Extension> extensions) {
		for (Extension extension : extensions) {
			if (extension instanceof AbstractProgressionExtension) {
				progressionPoint.register((AbstractProgressionExtension)extension);
			} else if (extension instanceof FeatureAbstractionExtension) {
				featureAbstractionPoint.register((FeatureAbstractionExtension) extension);
			} else if (extension instanceof LearningExtension) {
				learningPoint.register((LearningExtension) extension);
			}
		}
	}
	
	public void unregisterExtension(List<Extension> extensions) {
		for (Extension extension : extensions) {
			if (extension instanceof AbstractProgressionExtension) {
				progressionPoint.unregister((AbstractProgressionExtension) extension);
			} else if (extension instanceof FeatureAbstractionExtension) {
				featureAbstractionPoint.unregister((FeatureAbstractionExtension) extension);
			} else if (extension instanceof LearningExtension) {
				learningPoint.unregister((LearningExtension) extension);
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

	public void onContextArrival(BaseFactor context) {
		// TODO Auto-generated method stub
		
	}

	public void onContextExpired(BaseFactor context) {
		// TODO Auto-generated method stub
		
	}

	public void onContextDeleted(BaseFactor context) {
		// TODO Auto-generated method stub
		
	}
}
