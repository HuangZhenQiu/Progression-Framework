package edu.uci.eecs.wukong.framework.pipeline;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

import edu.uci.eecs.wukong.framework.api.Closable;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.entity.ModelEntity;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.factor.SceneManager;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;

public class BasicPipeline extends Pipeline {
	private final static Logger LOGGER = LoggerFactory.getLogger(BasicPipeline.class);
	private FeatureExtractionExtensionPoint featureExtractionPoint;
	private ProgressionExtensionPoint progressionPoint;
	private LearningExtensionPoint learningPoint;
	
	@VisibleForTesting
	public BasicPipeline() {
		
	}
	
	public BasicPipeline(SceneManager sceneManager, FeatureChoosers featureChoosers) {
		super(sceneManager, featureChoosers);
		this.progressionPoint = new ProgressionExtensionPoint(this);
		this.featureExtractionPoint = new FeatureExtractionExtensionPoint(featureChoosers, this);
		this.learningPoint = new LearningExtensionPoint(this);
		
		// Build up the trigger graph for messaging routing
		this.addExentionPoint(featureExtractionPoint);
		this.addExentionPoint(learningPoint);
		this.addExentionPoint(progressionPoint);
		this.addPipelineLink(featureExtractionPoint, learningPoint, FeatureEntity.class);
		this.addPipelineLink(featureExtractionPoint, progressionPoint, FeatureEntity.class);
		this.addPipelineLink(learningPoint, progressionPoint, ModelEntity.class);
		
		// Subscribe factors
		this.sceneManager.subsribeFactor(learningPoint);
		this.sceneManager.subsribeFactor(progressionPoint);
	}
	
	@Override
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
				} else if (extension instanceof FeatureExtractionExtension) {
					featureExtractionPoint.register((FeatureExtractionExtension) extension);
				} else if (extension instanceof LearningExtension) {
					learningPoint.register((LearningExtension) extension);
				}
			}
		}
	}
	
	@Override
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
					
				} else if (extension instanceof FeatureExtractionExtension) {
					featureExtractionPoint.unregister((FeatureExtractionExtension) extension);
				} else if (extension instanceof LearningExtension) {
					learningPoint.unregister((LearningExtension) extension);
				}
			}
		}
	}
}
