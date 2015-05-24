package edu.uci.eecs.wukong.framework.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.extension.FeatureAbstractionExtension;
import edu.uci.eecs.wukong.framework.manager.BufferManager;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class FeatureAbstractionPoint extends ExtensionPoint<FeatureAbstractionExtension> implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(ProgressionExtensionPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	private List<FeatureAbstractionExtension> extentions;
	private BufferManager bufferManager; 
	
	public FeatureAbstractionPoint (BufferManager  bufferManage) {
		this.bufferManager = bufferManager;
		this.extensions = new ArrayList<FeatureAbstractionExtension>();
	}
	
	public synchronized appendExtension(FeatureAbstractionExtension extension) {
		this.extensions.add(extension);
	}
	
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
