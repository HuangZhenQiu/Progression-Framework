package edu.uci.eecs.wukong.edge.eeg;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.extension.AbstractProgressionExtension;

import libsvm.svm_model;
import libsvm.svm;
import libsvm.svm_node;

public class EEGProgressionExtension extends AbstractProgressionExtension<EEGPrClass> implements
	Executable<Number>, Initiable {
	private static Logger logger = LoggerFactory.getLogger(EEGProgressionExtension.class);
	private static String MODEL_PATH = "";
	private svm_model model = null;

	public EEGProgressionExtension(EEGPrClass plugin) {
		super(plugin);
	}

	@Override
	public void execute(List<Number> data, ExecutionContext context) {
		logger.info("EEGProgressionExtension is executed!");
		StringBuilder builder = new StringBuilder();
		svm_node[] nodes = new svm_node[2];
		for (int i = 0; i < data.size(); i++) {
			builder.append(data.get(i).toString());
		}
		
		nodes[0] = new svm_node();
		nodes[0].index = 0;
		nodes[0].value = (double)data.get(0);
		nodes[1] = new svm_node();
		nodes[1].index = 0;
		nodes[1].value = (double)data.get(1);
		
		
		logger.info("EEGProgressionExtension recevied wave power " + builder.toString());
		double probability = svm.svm_predict(model, nodes);
		if (probability > 0.6) {
			this.getPrClass().setOutput(true);
		}
	}

	@Override
	public boolean init() {
		try {
			model = svm.svm_load_model(MODEL_PATH);
		} catch (Exception e) {
			logger.error("Fail to initialize svm model");
			return false;
		}
		
		return true;
	}
}
