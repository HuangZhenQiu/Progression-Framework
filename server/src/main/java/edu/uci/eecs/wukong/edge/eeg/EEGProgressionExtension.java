package edu.uci.eecs.wukong.edge.eeg;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;

public class EEGProgressionExtension extends AbstractProgressionExtension<EEGPrClass> implements
	Executable<Number>, Initiable {
	private static Logger logger = LoggerFactory.getLogger(EEGProgressionExtension.class);

	public EEGProgressionExtension(EEGPrClass plugin) {
		super(plugin);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(List<Number> data, ExecutionContext context) {
		// TODO (Mohammed, feed data into your model and make decision)
		logger.info("EEGProgressionExtension is executed!");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			builder.append(data.get(i).toString());
		}
		
		logger.info("EEGProgressionExtension recevied wave power " + builder.toString());
	}

	@Override
	public void init() {
		// TODO (Mohammed, initialize your model here)
	}
}
