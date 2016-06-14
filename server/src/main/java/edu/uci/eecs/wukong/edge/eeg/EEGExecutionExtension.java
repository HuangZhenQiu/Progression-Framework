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
/**
 * 
 * https://www.csie.ntu.edu.tw/~r94100/libsvm-2.8/README
 * 
 * In this model, 0 represents eye close, 1 represents eye open.
 * 
 * @author peter
 *
 */
public class EEGExecutionExtension extends AbstractExecutionExtension<EEGPrClass> implements
	Executable<Number>, Initiable {
	private static Configuration configuration = Configuration.getInstance();
	private static Logger logger = LoggerFactory.getLogger(EEGExecutionExtension.class);
	private static String MODEL_PATH = configuration.getProgressionHome() + "/tool/svm/eeg/model.txt";
	private static String DATA_PATH = configuration.getProgressionHome() + "/tool/svm/eeg/train.txt";
	private svm_model model = null;
	private double[] labelProbabilities = new double[2];

	public EEGExecutionExtension(EEGPrClass plugin) {
		super(plugin);
	}

	@Override
	public void execute(List<Number> data, ExecutionContext context) {
		logger.info("EEGProgressionExtension is executed!");
		if (data.size() == 4) {
			StringBuilder builder = new StringBuilder();
			svm_node[] nodes = new svm_node[4];
			for (int i = 0; i < data.size(); i++) {
				builder.append(data.get(i).toString());
				nodes[i] = new svm_node();
				nodes[i].index = i;
				nodes[i].value = (double)data.get(i);
			}
			
			logger.info("EEGProgressionExtension recevied wave power " + builder.toString());
			double probability = svm.svm_predict_probability(model, nodes, labelProbabilities);
			logger.info("Label 0: " + labelProbabilities[0] + " and Label 1: " + labelProbabilities[1]);
			// 0.0 represents close
			if (labelProbabilities[0] >= 0.88) {
				logger.info("Set output to true, when eye close");
				this.prClass.setOutput(true);
			} else {
				this.prClass.setOutput(false);
			}
		}
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
			File file = new File(DATA_PATH);
			BufferedReader reader =  new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			svm_node[] nodes = new svm_node[4];
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = new svm_node();
				nodes[i].index = i;
			}
			
		    int[] labels = new int[2];
		    svm.svm_get_labels(model,labels);
			while (line!= null && !line.isEmpty()) {
				System.out.println(line);
				String[] features =  line.split(" ");
				if (features.length >= 2) {
					for (int i = 0; i < nodes.length; i++) {
						nodes[i].value = 0;
					}
					
					for (int i = 1; i < features.length; i++) {
						String[] value = features[i].split(":");
						int index = Integer.parseInt(value[0]);
						nodes[index].value = Double.parseDouble(value[1]);
					}

					System.out.println(svm.svm_predict(model, nodes));
					svm.svm_predict_probability(model, nodes, labelProbabilities);
					for (int i = 0; i < labels.length; i++) {
						System.out.println("Label " + labels[i] + ": " + labelProbabilities[i]);
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
