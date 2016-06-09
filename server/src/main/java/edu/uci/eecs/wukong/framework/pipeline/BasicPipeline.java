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
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.extension.FeatureExtractionExtension;
import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.factor.SceneManager;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;

public class BasicPipeline extends Pipeline {
	private final static Logger LOGGER = LoggerFactory.getLogger(BasicPipeline.class);
	private FeatureExtractionExtensionPoint featureExtractionPoint;
	private ProgressionExtensionPoint progressionPoint;
	private LearningExtensionPoint learningPoint;
	
	@VisibleForTesting
	public BasicPipeline() {
		
	}
	
	public BasicPipeline(SceneManager sceneManager, FeatureChoosers featureChoosers, PipelineMetrics pieplineMetrics) {
		super(sceneManager, featureChoosers, pieplineMetrics);
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
	public void registerExtension(WuObjectModel model) {
		if (model.getExtensions() != null && !model.getExtensions().isEmpty()) {
			for (Extension extension : model.getExtensions()) {
				if (extension instanceof AbstractExecutionExtension) {
					progressionPoint.register((AbstractExecutionExtension<? extends PipelinePrClass>) extension);
				} else if (extension instanceof FeatureExtractionExtension) {
					featureExtractionPoint.register((FeatureExtractionExtension) extension);
				} else if (extension instanceof LearningExtension) {
					learningPoint.register((LearningExtension) extension);
				}
			}
		}
	}
	
	@Override
	public void unregisterExtension(WuObjectModel model) {
		if (model.getExtensions() != null && !model.getExtensions().isEmpty()) {
			for (Extension extension : model.getExtensions()) {
				if (extension instanceof AbstractExecutionExtension) {
					AbstractExecutionExtension<? extends PipelinePrClass> progressionExtension = (AbstractExecutionExtension) extension;
					progressionPoint.unregister((AbstractExecutionExtension) extension);
				} else if (extension instanceof FeatureExtractionExtension) {
					featureExtractionPoint.unregister((FeatureExtractionExtension) extension);
				} else if (extension instanceof LearningExtension) {
					learningPoint.unregister((LearningExtension) extension);
				}
			}
		}
	}
}
