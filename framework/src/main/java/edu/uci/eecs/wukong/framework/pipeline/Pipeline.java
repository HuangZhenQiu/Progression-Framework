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
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.entity.ModelEntity;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.graph.Graph;
import edu.uci.eecs.wukong.framework.graph.Link;

import java.util.List;
import java.lang.Thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class Pipeline extends Graph implements FactorListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);
	private SceneManager sceneManager;
	private ConfigurationManager configurationManager;
	private BufferManager bufferManager;
	private FeatureChoosers featureChoosers;
	private FeatureExtractionExtensionPoint featureExtractionPoint;
	private ProgressionExtensionPoint progressionPoint;
	private LearningExtensionPoint learningPoint;
	
	@VisibleForTesting
	public Pipeline() {
		
	}
	
	public Pipeline(SceneManager sceneManager, FeatureChoosers featureChoosers) {
		this.sceneManager = sceneManager;
		this.configurationManager = ConfigurationManager.getInstance();
		this.featureChoosers = featureChoosers;
		this.progressionPoint = new ProgressionExtensionPoint(this);
		this.featureExtractionPoint = new FeatureExtractionExtensionPoint(featureChoosers, this);
		this.learningPoint = new LearningExtensionPoint(this);
		
		// Build up the trigger graph for messaging routing
		this.addNode(featureExtractionPoint);
		this.addNode(learningPoint);
		this.addNode(progressionPoint);
		this.addLink(new Link(featureExtractionPoint, learningPoint, FeatureEntity.class));
		this.addLink(new Link(featureExtractionPoint, progressionPoint, FeatureEntity.class));
		this.addLink(new Link(learningPoint, progressionPoint, ModelEntity.class));
		
		// Subscribe factors
		this.sceneManager.subsribeFactor(learningPoint);
		this.sceneManager.subsribeFactor(progressionPoint);
		this.sceneManager.subsribeFactor(this);
	}
	
	public ExecutionContext getCurrentContext(PrClass prClass) {
		return sceneManager.getPluginExecutionContext(prClass);
	}
	
	public void registerExtension(List<Extension> extensions) {
		if (extensions != null && !extensions.isEmpty()) {
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
							+ progressionExtension.getPrClass() + ", base of exception: " + e.toString());
					}
				} else if (extension instanceof FeatureAbstractionExtension) {
					featureExtractionPoint.register((FeatureAbstractionExtension) extension);
				} else if (extension instanceof LearningExtension) {
					learningPoint.register((LearningExtension) extension);
				}
			}
		}
	}
	
	public void unregisterExtension(List<Extension> extensions) {
		if (extensions != null && !extensions.isEmpty()) {
			for (Extension extension : extensions) {
				if (extension instanceof AbstractProgressionExtension) {
					AbstractProgressionExtension progressionExtension = (AbstractProgressionExtension) extension;
					try {
						// Call the close function 
						if (extension instanceof Closable) {
							Closable initiable = (Closable) extension;
							initiable.close();
						}
						progressionPoint.unregister((AbstractProgressionExtension) extension);
					} catch (Exception e) {
						LOGGER.info("Fail to register progression extension for plugin "
							+ progressionExtension.getPrClass() + ", base of exception: " + e.toString());
					}
					
				} else if (extension instanceof FeatureAbstractionExtension) {
					featureExtractionPoint.unregister((FeatureAbstractionExtension) extension);
				} else if (extension instanceof LearningExtension) {
					learningPoint.unregister((LearningExtension) extension);
				}
			}
		}
	}
	
	public void start() {
		Thread featureAbstraction = new Thread(featureExtractionPoint);
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
