package edu.uci.eecs.wukong.edge.eeg;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.api.Initiable;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;
import edu.uci.eecs.wukong.framework.util.Configuration;
import libsvm.svm_model;
import libsvm.svm;
import libsvm.svm_node;

public class EEGExecutionExtension extends AbstractExecutionExtension<EEGPrClass> implements
	Executable<Number>, Initiable {
	private static Configuration configuration = Configuration.getInstance();
	private static Logger logger = LoggerFactory.getLogger(EEGExecutionExtension.class);
	private static String MODEL_PATH = configuration.getProgressionHome() + "/tool/svm/eeg/model.txt";
	private svm_model model = null;
	private double[] labelProbabilities = new double[2];

	public EEGExecutionExtension(EEGPrClass plugin) {
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
		nodes[1].index = 1;
		nodes[1].value = (double)data.get(1);
		
		
		logger.info("EEGProgressionExtension recevied wave power " + builder.toString());
		double probability = svm.svm_predict_probability(model, nodes, labelProbabilities);
		logger.info("Label 0: " + labelProbabilities[0] + " and Label 1: " + labelProbabilities[1]);
	}

	@Override
	public boolean init() {
		try {
			model = svm.svm_load_model(MODEL_PATH);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("Fail to initialize svm model");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Used only for internal offline test, not for runtime testing
	 * 
	 */
	private void testClassification() {
		try {
			File file = new File(MODEL_PATH);
			BufferedReader reader =  new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			svm_node[] nodes = new svm_node[2];
			nodes[0] = new svm_node();
			nodes[1] = new svm_node();
			nodes[0].index = 0;
			nodes[1].index = 1;

			while (line!= null && !line.isEmpty()) {
				String[] features =  line.split(" ");
				if (features.length > 2) {
					String[] alpha = features[1].split(":");
					String[] beta = features[2].split(":");
					if (alpha.length == 2 && beta.length == 2) {
						nodes[0].value = Double.parseDouble(alpha[1]);
						nodes[1].value = Double.parseDouble(beta[1]);
						System.out.println(svm.svm_predict(model, nodes));
					}
				}
				line = reader.readLine();
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	
	public static void main(String args[]) {
		EEGExecutionExtension extension = new EEGExecutionExtension(null);
		extension.init();
		extension.testClassification();
	}
}
