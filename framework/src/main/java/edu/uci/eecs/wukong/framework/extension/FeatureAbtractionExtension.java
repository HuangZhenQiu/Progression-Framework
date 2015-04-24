package edu.uci.eecs.wukong.framework.extension;

import java.util.List;
import java.util.Map;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;
import edu.uci.eecs.wukong.framework.operator.Operator;

/**
 * 
 * 
 * @author Peter
 *
 */
public interface FeatureAbtractionExtension {
	public List<Operator> registerOperators(Map<String, LogicalKey> logicId);
}
