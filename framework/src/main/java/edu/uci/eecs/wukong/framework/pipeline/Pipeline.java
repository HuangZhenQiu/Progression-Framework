package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.api.Closable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.manager.SceneManager;
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
	private SceneManager sceneManager;
	private ConfigurationManager configurationManager;
	private BufferManager bufferManager;
	private FeatureChoosers featureChoosers;
	private FeatureAbstractionExtensionPoint featureAbstractionPoint;
	private ProgressionExtensionPoint progressionPoint;
	private LearningExtensionPoint learningPoint;
	
	@VisibleForTesting
	public Pipeline() {
		
	}
	
	public Pipeline(SceneManager sceneManager,
			ConfigurationManager configuraionManager, FeatureChoosers featureChoosers) {
		this.sceneManager = sceneManager;
		this.configurationManager = configuraionManager;
		this.featureChoosers = featureChoosers;
		this.progressionPoint = new ProgressionExtensionPoint(configuraionManager, this);
		this.featureAbstractionPoint = new FeatureAbstractionExtensionPoint(featureChoosers, this);
		this.learningPoint = new LearningExtensionPoint(this);
		this.sceneManager.subsribeFactor(learningPoint);
		this.sceneManager.subsribeFactor(progressionPoint);
		this.sceneManager.subsribeFactor(this);
	}
	
	public ExecutionContext getCurrentContext(PrClass prClass) {
		return sceneManager.getPluginExecutionContext(prClass);
	}
	
	public void dipatchModel(String appId, Object model) throws Exception {
		progressionPoint.applyModel(appId, model);
	}
	
	public void registerExtension(List<Extension> extensions) {
		for (Extension extension : extensions) {
			if (extension instanceof AbstractProgressionExtension) {
				AbstractProgressionExtension progressionExtension = (AbstractProgressionExtension) extension;
				try {
					// Call the initial function 
					if (extension instanceof Initiable) {
						Initiable initiable = (Initiable) extension;
						initiable.init();
					}
					progressionPoint.register(progressionExtension);
				} catch (Exception e) {
					LOGGER.info("Fail to register progression extension for plugin "
						+ progressionExtension.getPlugin() + ", base of exception: " + e.toString());
				}
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
				AbstractProgressionExtension progressionExtension = (AbstractProgressionExtension) extension;
				try {
					// Call the initial function 
					if (extension instanceof Closable) {
						Closable initiable = (Closable) extension;
						initiable.close();
					}
					progressionPoint.unregister((AbstractProgressionExtension) extension);
				} catch (Exception e) {
					LOGGER.info("Fail to register progression extension for plugin "
						+ progressionExtension.getPlugin() + ", base of exception: " + e.toString());
				}
				
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

	public void onFactorArrival(BaseFactor context) {
		// TODO Auto-generated method stub
		
	}

	public void onTopicExpired(BaseFactor context) {
		// TODO Auto-generated method stub
		
	}

	public void onTopicDeleted(BaseFactor context) {
		// TODO Auto-generated method stub
		
	}
}
