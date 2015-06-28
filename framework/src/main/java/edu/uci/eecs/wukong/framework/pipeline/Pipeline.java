package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.manager.ContextManager;
import edu.uci.eecs.wukong.framework.plugin.Plugin;
import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ContextListener;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.Extension;
import edu.uci.eecs.wukong.framework.extension.impl.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.extension.impl.LearningExtension;
import edu.uci.eecs.wukong.framework.extension.impl.ProgressionExtension;

import java.util.ArrayList;
import java.util.List;
import java.lang.Thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class Pipeline implements ContextListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);
	private ContextManager contextManager;
	private ConfigurationManager configurationManager;
	private BufferManager bufferManager;
	private FeatureAbstractionPoint featureAbstractionPoint;
	private ProgressionExtensionPoint progressionPoint;
	private LearningPoint learningPoint;
	private ExecutionContext executionContext;
	
	@VisibleForTesting
	public Pipeline() {
		
	}
	
	public Pipeline(ContextManager contextManager,
			ConfigurationManager configuraionManager) {
		this.contextManager = contextManager;
		this.configurationManager = configuraionManager;
		this.bufferManager = new BufferManager();
		this.progressionPoint = new ProgressionExtensionPoint(configuraionManager, this);
		this.featureAbstractionPoint = new FeatureAbstractionPoint(bufferManager, this);
		this.learningPoint = new LearningPoint(this);
		this.contextManager.subsribeContext(learningPoint);
		this.contextManager.subsribeContext(this);
		this.executionContext = new ExecutionContext();
	}
	
	public ExecutionContext getCurrentContext(Plugin plugin) {
		ExecutionContext context = new ExecutionContext();
		for (String topicId : plugin.registerContext()) {
			Context cont = executionContext.getContext(topicId);
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

	public void onContextArrival(Context context) {
		// TODO Auto-generated method stub
		
	}

	public void onContextExpired(Context context) {
		// TODO Auto-generated method stub
		
	}

	public void onContextDeleted(Context context) {
		// TODO Auto-generated method stub
		
	}
}
