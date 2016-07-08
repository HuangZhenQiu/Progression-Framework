package edu.uci.eecs.wukong.framework.reconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.uci.eecs.wukong.framework.predict.Predict;
import edu.uci.eecs.wukong.framework.predict.PredictOperatorType;
import edu.uci.eecs.wukong.framework.predict.PredictType;
import junit.framework.TestCase;

public class ConfigurationManagerTest extends TestCase {
	private ConfigurationManager manager = ConfigurationManager.getInstance();
	
	@Test
	public void testRemappingService() {
		List<Predict> predicts = new ArrayList<Predict> ();
		Predict predict = new Predict(1, PredictType.Location, PredictOperatorType.EQ, "/WuKong");
		predicts.add(predict);
		manager.remapping("0cbc6611f5540bd0809a388dc95a615b", predicts);
	}
}
