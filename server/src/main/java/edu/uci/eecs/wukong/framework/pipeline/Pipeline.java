package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.buffer.BufferManager;
import edu.uci.eecs.wukong.framework.prclass.PipelinePrClass;
import edu.uci.eecs.wukong.framework.reconfig.ConfigurationManager;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.factor.SceneManager;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.graph.Graph;
import edu.uci.eecs.wukong.framework.graph.Link;
import edu.uci.eecs.wukong.framework.graph.Node;
import edu.uci.eecs.wukong.framework.model.WuObjectModel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public abstract class Pipeline extends Graph implements FactorListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);
	protected SceneManager sceneManager;
	protected ConfigurationManager configurationManager;
	protected BufferManager bufferManager;
	protected FeatureChoosers featureChoosers;
	protected ScheduledExecutorService executor;
	protected PipelineMetrics pipelineMetrics;
	
	@VisibleForTesting
	public Pipeline() {
		
	}
	
	public Pipeline(SceneManager sceneManager, FeatureChoosers featureChoosers, PipelineMetrics pipelineMetrics) {
		this.sceneManager = sceneManager;
		this.configurationManager = ConfigurationManager.getInstance();
		this.featureChoosers = featureChoosers;
		this.sceneManager.subsribeFactor(this);
		this.pipelineMetrics = pipelineMetrics;
	}
	
	public void addExentionPoint(ExtensionPoint<?> point) {
		pipelineMetrics.addExtensionGauge(point);
		this.addNode(point);
	}
	
	public void addPipelineLink(ExtensionPoint<?> source, ExtensionPoint<?> sink, Class<?> type) {
		this.addLink(new Link(source, sink, type));
	}
	
	public ExecutionContext getCurrentContext(PipelinePrClass prClass) {
		return sceneManager.getPluginExecutionContext(prClass);
	}
	
	public abstract void registerExtension(WuObjectModel model);
	
	public abstract void unregisterExtension(WuObjectModel model);
	
	public void start() {
		this.executor =  Executors.newScheduledThreadPool(nodes.size());
		for (Node node : nodes) {
			executor.scheduleAtFixedRate(node, 0, 100, TimeUnit.MILLISECONDS);
		}
		
		LOGGER.info("Progression Pipeline get started.");
	}
	
	public void shutdown() {
		for (Node node : nodes) {
			ExtensionPoint point = (ExtensionPoint) node;
			point.shutdown();
		}
		executor.shutdown();
	}
	
	public void markQueueSize(String name, int size) {
		pipelineMetrics.setExtensonQueuLag(name, size);
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
