package edu.uci.eecs.wukong.edge.activity;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.eecs.wukong.framework.api.Executable;
import edu.uci.eecs.wukong.framework.api.ExecutionContext;
import edu.uci.eecs.wukong.framework.extension.AbstractExecutionExtension;

import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm;

public class ActivityExecutionExtension extends AbstractExecutionExtension<ActivityRecgonitionPrClass>
	implements Executable<Short> {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ActivityExecutionExtension.class);
	private static final String MODEL_PATH = "/Users/peter/Desktop/Classification/java/model.txt";
	private static final int PREDICT_PROBABILITY = 1;
	private svm_model model;
	public ActivityExecutionExtension(ActivityRecgonitionPrClass plugin) {
		super(plugin);
		try {
			model = svm.svm_load_model(MODEL_PATH);
			if (model == null)
			{
				LOGGER.info("can't open model file " + MODEL_PATH);
			}
			
			if(PREDICT_PROBABILITY == 1) {
				if(svm.svm_check_probability_model(model)==0)
				{
					LOGGER.info("Model does not support probabiliy estimates\n");
				}
			} else {
				if(svm.svm_check_probability_model(model)!=0)
				{
					LOGGER.error("Model supports probability estimates, but disabled in prediction.\n");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Fail to load activity progression model.");
		}
	}

	@Override
	public void execute(List<Short> features, ExecutionContext context) {
		if (model != null) {
			svm_node[] x = new svm_node[features.size()];
			for (int i = 0; i < features.size(); i++) {
				x[i] = new svm_node();
				x[i].index = i;
				x[i].value = features.get(i);
			}
			svm.svm_predict(model, x);
			this.getPrClass();
		}
	}

}
