package edu.uci.eecs.wukong.edge.buffer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.prclass.EdgePrClass;

public class BufferExecutionExtension extends AbstractExecutionExtension<BufferEnabledEdgeClass> implements Executable {
	private static Logger logger = LoggerFactory.getLogger(BufferExecutionExtension.class);
	public BufferExecutionExtension(BufferEnabledEdgeClass plugin) {
		super(plugin);
	}

	@Override
	public void execute(List data, ExecutionContext context) {
		
		int i = 1;
		for (Object object : data) {
			logger.info("Feature data i " + i + " equals " + object);
			i ++;
		}
		logger.info("BufferProgressionExtension received feature data");
	}
}
