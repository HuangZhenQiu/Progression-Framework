package edu.uci.eecs.wukong.prclass.buffer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;
import edu.uci.eecs.wukong.framework.prclass.PrClass;

public class BufferProgressionExtension extends AbstractProgressionExtension implements Executable {
	private static Logger logger = LoggerFactory.getLogger(BufferProgressionExtension.class);
	public BufferProgressionExtension(PrClass plugin) {
		super(plugin);
	}

	@Override
	public void execute(List data, ExecutionContext context) {
		logger.info("BufferProgressionExtension received feature data");
	}
}
