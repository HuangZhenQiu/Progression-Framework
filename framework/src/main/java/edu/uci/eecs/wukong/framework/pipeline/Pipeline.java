package edu.uci.eecs.wukong.framework.pipeline;

import edu.uci.eecs.wukong.framework.manager.ConfigurationManager;
import edu.uci.eecs.wukong.framework.manager.ContextManager;
import edu.uci.eecs.wukong.framework.extension.Extension;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;

import java.util.List;
import java.lang.Thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Pipeline {
	private final static Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);
	private ContextManager contextManager;
	private ConfigurationManager configurationManager;
	private ProgressionExtensionPoint progressionPoint;
	
	public Pipeline(ContextManager contextManager, ConfigurationManager configuraionManager) {
		this.contextManager = contextManager;
		this.configurationManager = configuraionManager;
		this.progressionPoint = new ProgressionExtensionPoint(configuraionManager);
	}
	
	public void registerExtension(List<Extension> extensions) {
		for (Extension extension : extensions) {
			if (extension instanceof ProgressionExtension) {
				progressionPoint.register((ProgressionExtension)extension);
			}
		}
	}
	
	public void start() {
		Thread thread = new Thread(progressionPoint);
		thread.start();
		LOGGER.info("Progression Pipeline get started.");
	}
}
