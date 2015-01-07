package edu.uci.eecs.wukong.plugin.demo;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.entity.ConfigurationEntity;
import edu.uci.eecs.wukong.framework.entity.FeatureEntity;
import edu.uci.eecs.wukong.framework.context.Context;
import edu.uci.eecs.wukong.framework.context.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.ProgressionExtension;

public class DemoProgressionExtension implements ProgressionExtension<FeatureEntity> {
	private static Logger logger = LoggerFactory.getLogger(DemoProgressionExtension.class);
	// Triggered by general data pipeline
	public  List<ConfigurationEntity> execute(List<FeatureEntity> data, ExecutionContext context) {
		return new ArrayList<ConfigurationEntity>();
	}
	
	// Triggered by context switch
	public  List<ConfigurationEntity> execute(Context context) {
		logger.info(DemoProgressionExtension.class.getName() + " received new context");
		return new ArrayList<ConfigurationEntity>();
	}
	
	// Triggered by timer
	public  List<ConfigurationEntity> execute() {
		return new ArrayList<ConfigurationEntity>();
	}
}
