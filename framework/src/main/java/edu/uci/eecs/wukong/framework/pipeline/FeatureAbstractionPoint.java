package edu.uci.eecs.wukong.framework.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.extension.impl.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class FeatureAbstractionPoint extends ExtensionPoint<FeatureAbstractionExtension> implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private BufferManager bufferManager; 
	
	public FeatureAbstractionPoint (BufferManager  bufferManage, Pipeline pipeline) {
		super(pipeline);
		this.bufferManager = bufferManager;
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
