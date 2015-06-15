package edu.uci.eecs.wukong.framework.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.extension.LearningExtension;
import edu.uci.eecs.wukong.framework.util.Configuration;

public class LearningPoint extends ExtensionPoint<LearningExtension> implements Runnable{
	private static Logger logger = LoggerFactory.getLogger(LearningPoint.class);
	private static Configuration configuration = Configuration.getInstance();
	
	public LearningPoint(Pipeline pipeline) {
		super(pipeline);
	}
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
