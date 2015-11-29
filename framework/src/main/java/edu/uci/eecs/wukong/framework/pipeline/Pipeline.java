package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Extension;
import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.manager.SceneManager;
import edu.uci.eecs.wukong.framework.prclass.PrClass;
import edu.uci.eecs.wukong.framework.select.FeatureChoosers;
import edu.uci.eecs.wukong.framework.factor.BaseFactor;
import edu.uci.eecs.wukong.framework.factor.FactorListener;
import edu.uci.eecs.wukong.framework.graph.ExtensionPoint;
import edu.uci.eecs.wukong.framework.graph.Graph;
import edu.uci.eecs.wukong.framework.graph.Link;
import edu.uci.eecs.wukong.framework.graph.Node;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public abstract class Pipeline extends Graph implements FactorListener{
	private final static Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);
	protected SceneManager sceneManager;
	protected ConfigurationManager configurationManager;
	protected BufferManager bufferManager;
	protected FeatureChoosers featureChoosers;
	protected ExecutorService executor;
	
	@VisibleForTesting
	public Pipeline() {
		
	}
	
	public Pipeline(SceneManager sceneManager, FeatureChoosers featureChoosers) {
		this.sceneManager = sceneManager;
		this.configurationManager = ConfigurationManager.getInstance();
		this.featureChoosers = featureChoosers;
		this.sceneManager.subsribeFactor(this);
	}
	
	public void addExentionPoint(ExtensionPoint<?> point) {
		this.addNode(point);
	}
	
	public void addPipelineLink(ExtensionPoint<?> source, ExtensionPoint<?> sink, Class<?> type) {
		this.addLink(new Link(source, sink, type));
	}
	
	public ExecutionContext getCurrentContext(PrClass prClass) {
		return sceneManager.getPluginExecutionContext(prClass);
	}
	
	public abstract void registerExtension(List<Extension> extensions);
	
	public abstract void unregisterExtension(List<Extension> extensions);
	
	public void start() {
		this.executor = Executors.newFixedThreadPool(nodes.size());
		for (Node node : nodes) {
			executor.execute(node);
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
