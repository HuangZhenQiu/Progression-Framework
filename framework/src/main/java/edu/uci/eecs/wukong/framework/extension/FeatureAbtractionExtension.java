package edu.uci.eecs.wukong.framework.extension;

import java.util.List;

import edu.uci.eecs.wukong.framework.ProgressionKey.LogicalKey;
import edu.uci.eecs.wukong.framework.operator.Operator;

/**
 * 
 * 
 * @author Peter
 *
 */
public interface FeatureAbtractionExtension<T> {
	public List<Operator<T>> registerOperators(List<LogicalKey> logicId);
}
